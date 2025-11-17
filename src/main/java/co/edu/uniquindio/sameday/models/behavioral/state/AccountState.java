package co.edu.uniquindio.sameday.models.behavioral.state;

import co.edu.uniquindio.sameday.models.UserAccount;

public interface AccountState {
    boolean canLogin(UserAccount account);

    void handleFailedLogin(UserAccount account);

    void handleSuccessfulLogin(UserAccount account);

    String getStateName();

    String getStateMessage();

}
