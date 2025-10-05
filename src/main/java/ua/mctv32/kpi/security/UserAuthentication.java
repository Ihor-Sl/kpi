package ua.mctv32.kpi.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import ua.mctv32.kpi.domain.Role;

import java.util.Collection;
import java.util.Set;

@Data
@AllArgsConstructor
public class UserAuthentication implements Authentication {

    private Long id;
    private String email;
    private Set<Role> role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role.stream().map(Enum::name).map(SimpleGrantedAuthority::new).toList();
    }

    @Override
    public String getName() {
        return email;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getDetails() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return email;
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
    }
}
