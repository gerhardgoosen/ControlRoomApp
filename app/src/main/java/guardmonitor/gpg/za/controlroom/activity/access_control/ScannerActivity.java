package guardmonitor.gpg.za.controlroom.activity.access_control;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import guardmonitor.gpg.za.controlroom.R;
import guardmonitor.gpg.za.geoutils.utils.NotificationUtils;

import static android.Manifest.permission.CAMERA;

/**
 * A login screen that offers login via email/password.
 */
public class ScannerActivity extends AppCompatActivity implements OnClickListener {
    private static final String TAG = "ScannerActivity";
    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_USE_CAMERA = 0;
    private View mScannerView;
    private Button scanBtn;
    private TextView formatTxt, contentTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scanner_activity);
        mScannerView = findViewById(R.id.scanner_view);


        scanBtn = (Button) findViewById(R.id.scan_button);
        scanBtn.setOnClickListener(this);

        formatTxt = (TextView) findViewById(R.id.scan_format);
        contentTxt = (TextView) findViewById(R.id.scan_content);


    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.scan_button) {
            if (mayUseCamera()) {
                IntentIntegrator scanIntegrator = new IntentIntegrator(this);
                scanIntegrator.initiateScan();
            }
        }
    }


    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        //retrieve scan result
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null) {
            //we have a result
            String scanContent = scanningResult.getContents();
            String scanFormat = scanningResult.getFormatName();

            formatTxt.setText("FORMAT: " + scanFormat);
            contentTxt.setText("CONTENT: " + scanContent);

        } else {
            NotificationUtils.makeToast(getApplicationContext(), "No scan data received!", 0);
        }
    }


    private boolean mayUseCamera() {
        Log.v(TAG, "mayRequestContacts");
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(CAMERA) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(CAMERA)) {
            Snackbar.make(mScannerView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{CAMERA}, REQUEST_USE_CAMERA);
                        }
                    });
        } else {
            requestPermissions(new String[]{CAMERA}, REQUEST_USE_CAMERA);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.v(TAG, "onRequestPermissionsResult");
        if (requestCode == REQUEST_USE_CAMERA) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //open scanner
            }
        }
    }


}

