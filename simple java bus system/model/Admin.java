package model;

import java.util.Date;

public class Admin extends User {
    private final int adminId;
    private final String department;
    private final Date joinDate;

    public Admin(int adminId, String name, String email, String password, String department) {
        super(email, password);
        this.adminId = adminId;
        this.department = department;
        this.joinDate = new Date();
        setName(name);
    }

    public int getAdminId() {
        return adminId;
    }

    public String getDepartment() {
        return department;
    }

    public Date getJoinDate() {
        return joinDate;
    }

    @Override
    public String toString() {
        return String.format("Admin[ID=%d, Name=%s, Department=%s]", adminId, getName(), department);
    }
}