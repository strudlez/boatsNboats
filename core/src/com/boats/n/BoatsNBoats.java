package com.boats.n;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.boats.n.screens.GameScreen;

public class BoatsNBoats extends Game
{
	@Override
	public void create ()
    {
		setScreen(new GameScreen());
	}
}
