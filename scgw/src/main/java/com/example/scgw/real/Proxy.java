package com.example.scgw.real;

import java.util.ArrayList;
import java.util.List;

public class Proxy {
    private String path;
    private List<Method> methods = new ArrayList<>();

    public Proxy() {
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<Method> getMethods() {
        return methods;
    }

    public void setMethods(List<Method> methods) {
        this.methods = methods;
    }

    public static class Method {
        private String method;
        private List<Restriction> restrictions = new ArrayList<>();

        public Method() {}

        public String getMethod() {
            return method;
        }

        public void setMethod(String method) {
            this.method = method;
        }

        public List<Restriction> getRestrictions() {
            return restrictions;
        }

        public void setRestrictions(List<Restriction> restrictions) {
            this.restrictions = restrictions;
        }

        public static class Restriction {
            private List<String> territories;
            private List<String> allowedRoles = new ArrayList<>();

            public Restriction() {}

            public List<String> getTerritories() {
                return territories;
            }

            public void setTerritories(List<String> territories) {
                this.territories = territories;
            }

            public List<String> getAllowedRoles() {
                return allowedRoles;
            }

            public void setAllowedRoles(List<String> allowedRoles) {
                this.allowedRoles = allowedRoles;
            }
        }
    }
}