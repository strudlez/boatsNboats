package com.boats.n;

import com.badlogic.gdx.Game;
import com.boats.n.screens.GameScreen;

public class BoatsNBoats extends Game
{
    @Override
    public void create ()
    {
        setScreen(new GameScreen());
    }
}
