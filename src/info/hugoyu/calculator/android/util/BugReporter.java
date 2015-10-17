package info.hugoyu.calculator.android.util;

import android.app.Application;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;
import org.acra.sender.HttpSender;

@ReportsCrashes(
	    formUri = "https://hugo00618.cloudant.com/acra-calculator/_design/acra-storage/_update/report",
	    reportType = HttpSender.Type.JSON,
	    httpMethod = HttpSender.Method.POST,
	    formUriBasicAuthLogin = "mercrompreargoorelyoutwo",
	    formUriBasicAuthPassword = "C2GqvyxH7kleAbX13wGLHqvc",
	    formKey = "" // This is required for backward compatibility but not used
	    	    
	    // For paid
	    /*,mode = ReportingInteractionMode.DIALOG,
        resDialogText = R.string.crash_dialog_text,
        resDialogIcon = android.R.drawable.ic_dialog_alert,
        resDialogTitle = R.string.crash_dialog_title, 
        resDialogCommentPrompt = R.string.crash_dialog_comment_prompt,
        resDialogOkToast = R.string.crash_dialog_ok_toast*/
	)

	public class BugReporter extends Application {

	    @Override
	    public void onCreate() {
	        super.onCreate();
	        // The following line triggers the initialization of ACRA
	        ACRA.init(this);
	    }
	}