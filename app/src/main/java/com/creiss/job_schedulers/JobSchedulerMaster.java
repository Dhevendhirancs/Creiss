package com.creiss.job_schedulers;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;

public class JobSchedulerMaster {

    private Context mContext;

    public JobSchedulerMaster (Context mContext) {
        this.mContext = mContext;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public Boolean startSync() {
        JobScheduler jobScheduler = (JobScheduler) mContext.getSystemService(Context.JOB_SCHEDULER_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            jobScheduler.schedule(new JobInfo.Builder(2, new ComponentName(mContext, SyncFiles.class))
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .setPeriodic((60000*15),(60000*15))
                    .build()
            );
        } else {
            jobScheduler.schedule(new JobInfo.Builder(2, new ComponentName(mContext, SyncFiles.class))
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .setPeriodic((60000*15))
                    .build()
            );
        }
        return true;
    }

}
