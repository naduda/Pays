package pr.pays.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import pr.pays.model.User;

public class UserDetailsImpl extends User implements UserDetails {
	private static final long serialVersionUID = 1L;
	private static final String ROLE_USER = "ROLE_USER";
	
	private User uw;
	
	public UserDetailsImpl(User user) {
		uw = user;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return AuthorityUtils.createAuthorityList(ROLE_USER);
	}

	@Override
	public String getPassword() {
		return uw.getPassword();
	}

	@Override
	public String getUsername() {
		return uw.getLogin();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return !uw.isLocked();
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return uw.isActive();
	}
}