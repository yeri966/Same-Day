package co.edu.uniquindio.sameday.models;

import co.edu.uniquindio.sameday.models.behavioral.state.AccountState;
import co.edu.uniquindio.sameday.models.behavioral.state.ActiveState;

public class UserAccount {
    private String user;
    private String contrasenia;
    private Person person;
    private TypeUser typeUser;

    private AccountState accountState;
    private int failedAttempts;
    private long blockedTime;

    public UserAccount(String user, String contrasenia, Person person, TypeUser typeUser){
        this.user=user;
        this.contrasenia=contrasenia;
        this.person=person;
        this.typeUser=typeUser;
        this.accountState=new ActiveState();
        this.failedAttempts=0;
        this.blockedTime=0;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getContrasenia() {
        return contrasenia;
    }

    public void setContrasenia(String contrasenia) {
        this.contrasenia = contrasenia;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public TypeUser getTypeUser() {
        return typeUser;
    }

    public void setTypeUser(TypeUser typeUser) {
        this.typeUser = typeUser;
    }

    public boolean canLogin() {
        return accountState.canLogin(this);
    }

    public void handleFailedLogin() {
        accountState.handleFailedLogin(this);
    }

    public void handleSuccessfulLogin() {
        accountState.handleSuccessfulLogin(this);
    }

    public AccountState getAccountState() {
        return accountState;
    }

    public void setAccountState(AccountState accountState) {
        this.accountState = accountState;
    }

    public int getFailedAttempts() {
        return failedAttempts;
    }

    public void incrementFailedAttempts() {
        this.failedAttempts++;
    }

    public void resetFailedAttempts() {
        this.failedAttempts = 0;
    }

    public long getBlockedTime() {
        return blockedTime;
    }

    public void setBlockedTime(long blockedTime) {
        this.blockedTime = blockedTime;
    }
}

