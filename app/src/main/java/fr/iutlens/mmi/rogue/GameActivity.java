package fr.iutlens.mmi.rogue;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class GameActivity extends AppCompatActivity {

    static class GenerateTask extends AsyncTask<Integer, String, Integer>{
        final GameView gameView;

        GenerateTask(GameView gameView) {
            super();
            this.gameView = gameView;
        }

        @Override
        protected Integer doInBackground(Integer... params) {
            gameView.generate();
            return 0;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            gameView.invalidate();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final GameView gameView = findViewById(R.id.gameView);

        GenerateTask task = new GenerateTask(gameView);
        task.execute();
    }
}
