package com.caribou.auth.jwt;

import com.caribou.auth.domain.UserAccount;
import com.caribou.company.domain.CompanyEmployee;
import com.caribou.company.domain.Role;
import org.springframework.security.core.GrantedAuthority;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;


public class UserContext {

    private final String username;
    private final List<GrantedAuthority> authorities;

    private final UUID companyId;
    private final Role roleInCompany;
    private final UUID uid;

    private UserContext(UserAccount username, List<GrantedAuthority> authorities) {
        this.username = username.getEmail();
        this.authorities = authorities;
        this.companyId = null;
        this.roleInCompany = null;
        this.uid = username.getUid();
    }

    private UserContext(CompanyEmployee companyEmployee, List<GrantedAuthority> authorities) {
        this.username = companyEmployee.getMember().getEmail();
        this.authorities = authorities;
        this.companyId = companyEmployee.getCompany().getUid();
        this.roleInCompany = companyEmployee.getRole();
        this.uid = companyEmployee.getMember().getUid();
    }

    private UserContext(Builder builder) {
        username = builder.username;
        authorities = builder.authorities;
        companyId = builder.companyId;
        roleInCompany = builder.roleInCompany;
        uid = builder.uid;
    }

    public static UserContext create(@NotNull UserAccount username, List<GrantedAuthority> authorities) {
        return new UserContext(username, authorities);
    }

    public static UserContext create(@NotNull CompanyEmployee employee, List<GrantedAuthority> authorities) {
        return new UserContext(employee, authorities);
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getUsername() {
        return username;
    }

    public UUID getCompanyId() {
        return companyId;
    }

    public Role getRoleInCompany() {
        return roleInCompany;
    }

    public UUID getUid() {
        return uid;
    }

    public List<GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public static final class Builder {
        private String username;
        private List<GrantedAuthority> authorities;
        private UUID companyId;
        private Role roleInCompany;
        private UUID uid;

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

        public Builder companyId(UUID val) {
            companyId = val;
            return this;
        }

        public Builder roleInCompany(Role val) {
            roleInCompany = val;
            return this;
        }

        public Builder uid(UUID val) {
            uid = val;
            return this;
        }

        public UserContext build() {
            return new UserContext(this);
        }
    }
}
