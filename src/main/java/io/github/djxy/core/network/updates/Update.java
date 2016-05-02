package io.github.djxy.core.network.updates;

import io.github.djxy.core.network.github.Github;

/**
 * Created by Samuel on 2016-04-29.
 */
public abstract class Update implements Runnable {

    protected final Github github = new Github();
    private final boolean async;

    public Update(boolean async) {
        this.async = async;
    }

    public void check(){
        if(async)
            new Thread(this).start();
        else
            run();
    }

}
