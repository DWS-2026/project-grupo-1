package es.apexexpeditions.library.security.jwt;





// region =========== imports =================
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
// endregion





public class LoginRequest {
	// region =========== attributes =================
	// a03 patch: limit input size to prevent fake buffer overflows
	@NotBlank (message = "Username cannot be empty")
	@Size (max = 255, message = "Username exceeds maximum length")
	private String username;

	@NotBlank (message = "Password cannot be empty")
	@Size (max = 255, message = "Password exceeds maximum length")
	private String password;
	// endregion


	// region =========== constructor =================
	public LoginRequest() {
	}

	public LoginRequest (String username, String password) {
		this.username = username;
		this.password = password;
	}
	// endregion


	// region =========== getters and setters =================
	// username
	public String getUsername () { return username; }
	public void setUsername (String username) { this.username = username;
	}

	// password
	public String getPassword() { return password; }
	public void setPassword (String password) { this.password = password; }
	// end region
	// endregion


	// region =========== other methods =================
	@Override
	public String toString() {
		return "LoginRequest [username=" + username + ", password=" + password + "]";
	}
	// endregion
}