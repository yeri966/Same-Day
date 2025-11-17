package co.edu.uniquindio.sameday.models.behavioral.state;

import co.edu.uniquindio.sameday.models.UserAccount;

public class ActiveState implements AccountState{

    private static final int MAX_FAILED_ATTEMPTS=3;

    @Override
    public boolean canLogin(UserAccount account) {
        return true;
    }

    @Override
    public void handleFailedLogin(UserAccount account) {
        account.incrementFailedAttempts();
        if(account.getFailedAttempts()>=MAX_FAILED_ATTEMPTS){
            account.setAccountState(new BlockedState());
            account.setBlockedTime(System.currentTimeMillis());
        }

    }

    @Override
    public void handleSuccessfulLogin(UserAccount account) {
        account.resetFailedAttempts();

    }

    @Override
    public String getStateName() {
        return "ACTIVA";
    }

    @Override
    public String getStateMessage() {
        return "Cuenta activa";
    }
}
