package co.edu.uniquindio.sameday.models.behavioral.state;

import co.edu.uniquindio.sameday.models.UserAccount;

public class SuspendedState implements AccountState{

    private String suspensionReason;

    public SuspendedState(String reason){
        this.suspensionReason=reason;
    }
    public SuspendedState(){
        this.suspensionReason="Suspendida por el administrador";
    }

    @Override
    public boolean canLogin(UserAccount account) {
        return false;
    }

    @Override
    public void handleFailedLogin(UserAccount account) {
        //no hacer nada

    }

    @Override
    public void handleSuccessfulLogin(UserAccount account) {
        //No deberia llegar hasta aqui

    }

    @Override
    public String getStateName() {
        return "SUSPENDIDA";
    }

    @Override
    public String getStateMessage() {
        return "Cuenta suspendida: " + suspensionReason;
    }

    public String getSuspensionReason(){
        return suspensionReason;
    }
}
