package back.tickita.model.account;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class AccountDetails implements UserDetails {

    private final String nickName;
    private final String password;

    private final Collection<? extends GrantedAuthority> authorities;

    public AccountDetails(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        this.nickName = username;
        this.password = password;
        this.authorities = authorities;
    }

    // UserDetails 인터페이스의 메서드 구현
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return nickName;
    }

    // 나머지 UserDetails 인터페이스의 메서드 구현 (isEnabled, isAccountNonExpired 등)

    // 사용자 정의 메서드 등...
}