package co.edu.uniquindio.sameday.models.behavioral.state;

import co.edu.uniquindio.sameday.models.UserAccount;

public class BlockedState implements AccountState {

    private static final long BLOCK_DURATION_MS = 5 * 60 * 1000;

    @Override
    public boolean canLogin(UserAccount account) {
        if(System.currentTimeMillis()-account.getBlockedTime()>=BLOCK_DURATION_MS){
            account.setAccountState(new ActiveState());
            account.resetFailedAttempts();
            return true;
        }
        return false;
    }

    @Override
    public void handleFailedLogin(UserAccount account) {
        //No hacer nada la cuenta esta bloqueada

    }

    @Override
    public void handleSuccessfulLogin(UserAccount account) {
        //No deberia llegar aqui

    }

    @Override
    public String getStateName() {
        return "BLOQUEADA";
    }

    @Override
    public String getStateMessage() {
        return "Cuenta bloqueada temporalmente por multiples intentos fallidos";
    }

    public long getRemainingBlockTime(UserAccount account) {
        long elapsed = System.currentTimeMillis() - account.getBlockedTime();
        long remaining = BLOCK_DURATION_MS - elapsed;
        return Math.max(0, remaining);
    }
}
