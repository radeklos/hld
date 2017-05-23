package com.caribou.auth.jwt;

import com.caribou.company.domain.CompanyEmployee;
import com.caribou.company.domain.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.StringUtils;

import java.util.List;


public class UserContext {

    private final String username;
    private final List<GrantedAuthority> authorities;

    private final Long companyId;
    private final Role roleInCompany;

    private UserContext(String username, List<GrantedAuthority> authorities) {
        this.username = username;
        this.authorities = authorities;
        this.companyId = null;
        this.roleInCompany = null;
    }

    private UserContext(CompanyEmployee companyEmployee, List<GrantedAuthority> authorities) {
        this.username = companyEmployee.getMember().getEmail();
        this.authorities = authorities;
        this.companyId = companyEmployee.getCompany().getUid();
        this.roleInCompany = companyEmployee.getRole();
    }

    private UserContext(Builder builder) {
        username = builder.username;
        authorities = builder.authorities;
        companyId = builder.companyId;
        roleInCompany = builder.roleInCompany;
    }

    public static UserContext create(String username, List<GrantedAuthority> authorities) {
        if (StringUtils.isEmpty(username)) throw new IllegalArgumentException("Username is blank: " + username);
        return new UserContext(username, authorities);
    }

    public static UserContext create(CompanyEmployee employee, List<GrantedAuthority> authorities) {
        if (StringUtils.isEmpty(employee.getMember().getEmail()))
            throw new IllegalArgumentException("Username is blank: " + employee);
        return new UserContext(employee, authorities);
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getUsername() {
        return username;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public Role getRoleInCompany() {
        return roleInCompany;
    }

    public List<GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public static final class Builder {
        private String username;
        private List<GrantedAuthority> authorities;
        private Long companyId;
        private Role roleInCompany;

        private Builder() {
        }

        public Builder username(String val) {
            username = val;
            return this;
        }

        public Builder authorities(List<GrantedAuthority> val) {
            authorities = val;
            return this;
        }

        public Builder companyId(Long val) {
            companyId = val;
            return this;
        }

        public Builder roleInCompany(Role val) {
            roleInCompany = val;
            return this;
        }

        public UserContext build() {
            return new UserContext(this);
        }
    }
}
