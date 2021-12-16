package com.example.jacksonstateuniversity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler


import android.media.MediaPlayer
import android.net.Uri
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.MediaController
import android.widget.VideoView
import androidx.core.content.ContextCompat.startActivity
//import com.example.universityapp.utils.LoadingIcon
//import com.example.universityapp.utils.LoadingIcon
import java.util.*


class WelcomeIntro : AppCompatActivity() {

    var videoView: VideoView? = null

    var mediaController: MediaController? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome_intro)


        var studentEmail: Boolean = intent.getBooleanExtra("studentEmail",false)
        var facultyEmail: Boolean = intent.getBooleanExtra("facultyEmail",false)


        videoView = findViewById<View>(R.id.videoView) as VideoView?

        if (mediaController == null) {
            var mediaController = MediaController(this)
            mediaController!!.setAnchorView(this.videoView)
        }

        videoView!!.setMediaController(mediaController)
        videoView!!.setVideoURI(
            Uri.parse(
                "android.resource://" + packageName + "/" + R.raw.welcomeintro
            )
        )
        videoView!!.requestFocus()
        videoView!!.start()


        val song: MediaPlayer = MediaPlayer.create(this, R.raw.music)

        supportActionBar?.hide()
        song.start()

        Log.i("Faculty", "Faculty $facultyEmail")
        Log.i("Student", "Student $studentEmail")
        Handler(Looper.getMainLooper()).postDelayed({

            if(studentEmail){
                val intent = Intent(this@WelcomeIntro, MainActivity::class.java)
                finish()
            song.stop()
                videoView?.stopPlayback()
                startActivity(intent)
//
            }
            else if(facultyEmail){
                val intent = Intent(this@WelcomeIntro, FacultyMain::class.java)
                finish()
            song.stop()
                videoView?.stopPlayback()
                startActivity(intent)

            }

        }, 40000)
    }
}