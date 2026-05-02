package es.apexexpeditions.library.security.jwt;






// region =========== imports =================
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UserDetailsService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
// endregion






@Service
public class UserLoginService {
	// region =========== attributes =================
	private static final Logger log = LoggerFactory.getLogger (UserLoginService.class);
	private final AuthenticationManager authenticationManager;
	private final UserDetailsService userDetailsService;
	private final JwtTokenProvider jwtTokenProvider;
	// region =========== endregion =================


	// region =========== constructor =================
	public UserLoginService (AuthenticationManager authenticationManager, UserDetailsService userDetailsService, JwtTokenProvider jwtTokenProvider) {
		this.authenticationManager = authenticationManager;
		this.userDetailsService = userDetailsService;
		this.jwtTokenProvider = jwtTokenProvider;
	}
	// endregion


	// region =========== 1. login =================
	public ResponseEntity<AuthResponse> login (HttpServletResponse response, LoginRequest loginRequest) {
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);
		
		String username = loginRequest.getUsername();
		UserDetails user = userDetailsService.loadUserByUsername(username);

		HttpHeaders responseHeaders = new HttpHeaders();
		var newAccessToken = jwtTokenProvider.generateAccessToken(user);
		var newRefreshToken = jwtTokenProvider.generateRefreshToken(user);

		response.addCookie(buildTokenCookie(TokenType.ACCESS, newAccessToken));
		response.addCookie(buildTokenCookie(TokenType.REFRESH, newRefreshToken));

		AuthResponse loginResponse = new AuthResponse(AuthResponse.Status.SUCCESS,
				"Auth successful. Tokens are created in cookie.");
		return ResponseEntity.ok().headers(responseHeaders).body(loginResponse);
	}
	// endregion


	// region =========== 1. refresh =================
	public ResponseEntity<AuthResponse> refresh (HttpServletResponse response, String refreshToken) {
		try {
			var claims = jwtTokenProvider.validateToken(refreshToken);
			UserDetails user = userDetailsService.loadUserByUsername(claims.getSubject());

			var newAccessToken = jwtTokenProvider.generateAccessToken(user);
			response.addCookie(buildTokenCookie(TokenType.ACCESS, newAccessToken));

			AuthResponse loginResponse = new AuthResponse(AuthResponse.Status.SUCCESS,
					"Auth successful. Tokens are created in cookie.");
			return ResponseEntity.ok().body(loginResponse);

		} catch (Exception e) {
			log.error("Error while processing refresh token", e);
			AuthResponse loginResponse = new AuthResponse(AuthResponse.Status.FAILURE,
					"Failure while processing refresh token");
			return ResponseEntity.ok().body(loginResponse);
		}
	}
	// endregion


	// region =========== 3. logout =================
	public String logout(HttpServletResponse response) {
		SecurityContextHolder.clearContext();
		response.addCookie(removeTokenCookie(TokenType.ACCESS));
		response.addCookie(removeTokenCookie(TokenType.REFRESH));

		return "logout successfully";
	}
	// endregion


	// region =========== 4. buildTokenCookie =================
	/**
	 * builds secure http-only cookie for jwt tokens
	 */
	private Cookie buildTokenCookie (TokenType type, String token) {
		Cookie cookie = new Cookie(type.cookieName, token);
		cookie.setMaxAge ((int) type.duration.getSeconds());
		cookie.setHttpOnly (true);   // prevent js access (xss fix)
		cookie.setSecure (true);   // prevent transmission over http (mitm fix)
		cookie.setPath ("/");
		return cookie;
	}
	// endregion


	// region =========== 5. removeTokenCookie =================
	/**
	 * removes hwt cookie by overwriting it with an expired, empty secure cookie
	 */
	private Cookie removeTokenCookie (TokenType type){
		Cookie cookie = new Cookie(type.cookieName, "");
		cookie.setMaxAge (0);
		cookie.setHttpOnly (true);
		cookie.setSecure (true);   // must match original cookie flags to clear it properly
		cookie.setPath ("/");
		return cookie;
	}
	// endregion
}
