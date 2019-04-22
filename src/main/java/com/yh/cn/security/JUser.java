package com.yh.cn.security;

import com.yh.cn.entity.SysUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;

public class JUser extends SysUser implements UserDetails {


    /**
     * 权限
     */
    private Set<SimpleGrantedAuthority> permissions;


    public Set<SimpleGrantedAuthority> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<SimpleGrantedAuthority> permissions) {
        this.permissions = permissions;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return permissions;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
