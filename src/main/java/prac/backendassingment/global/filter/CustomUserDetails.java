package prac.backendassingment.global.filter;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import prac.backendassingment.global.enums.MemberRole;

import java.util.Collection;
import java.util.Collections;

public class CustomUserDetails implements UserDetails {

    private Long id;
    private Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(Long id, String memberRole){
        this.id = id;
        this.authorities =  Collections.singletonList(new SimpleGrantedAuthority(memberRole));;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return String.valueOf(id);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public Long getId(){
        return id;
    }
}
