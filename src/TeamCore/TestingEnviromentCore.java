package TeamCore;


import TeamCore.DefaultComponents.CoreComponent;

import java.util.ArrayList;

public final class TestingEnviromentCore extends TeamCore {
    public TestingEnviromentCore() {
    	super();
        this.activateInteractionLogging();
    }

    private String currentComponentName = "Init";
    public ArrayList<String> logs = new ArrayList<>();
    @Override
    public void logInteraction(String message){
        this.logs.add(this.currentComponentName + " - " + message);
    }

    public void setCurrentComponentName(String newName){
        this.currentComponentName = newName;
    }

    public void reset(){
        this.disableInteractionLogging();
        // weird ass workaround
        this.wipeComponents();
        this.superSecretFunc();
        this.update();
        this.activateInteractionLogging();
        }
}
