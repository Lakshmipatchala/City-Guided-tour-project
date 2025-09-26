package de.hft.activity;


import de.hft.R;
import de.hft.map.Map;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;

/**
 * this is the first class when application is launched
 * it handles creation and suspension of splash screen
 * @author virat
 *
 */
public class MobileCityGuideActivity extends Activity {
    protected boolean _active = true;
    protected int _splashTime = 2000;
    
    /** Called when the activity is first created. 
     * sets the time for splash screen and handles when to dismiss splash screen
     */
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        
        // thread for displaying the SplashScreen
        Thread splashTread = new Thread() {
            
        	/**
        	 * handles runtime of splash screen
        	 */
        	@Override
            public void run() {
                try {
                    int waited = 0;
                    while(_active && (waited < _splashTime)) {
                        sleep(100);
                        if(_active) {
                            waited += 100;
                        }
                    }
                } catch(InterruptedException e) {
                    // do nothing
                } finally {
                    finish();
                    startActivity(new Intent(MobileCityGuideActivity.this,MainMenuActivity.class));
                    stop();
                }
            }
        };
        splashTread.start();
    }
    
    /**
     * suspends splash screen when screen is touched 
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            _active = false;
        }
        return true;
    }
}