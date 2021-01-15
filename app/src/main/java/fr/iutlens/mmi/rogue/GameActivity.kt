package fr.iutlens.mmi.rogue

import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity

class GameActivity : AppCompatActivity() {
    internal class GenerateTask(val gameView: GameView) : AsyncTask<Int?, String?, Int>() {
        override fun onPostExecute(integer: Int) {
            super.onPostExecute(integer)
            gameView.invalidate()
        }

        override fun doInBackground(vararg p0: Int?): Int {
            gameView.generate()
            return 0
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val gameView = findViewById<GameView>(R.id.gameView)
        val task = GenerateTask(gameView)
        task.execute()
    }
}