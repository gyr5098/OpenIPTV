package com.openiptv.code.player;

import android.content.Context;
import android.util.Log;

import com.google.android.exoplayer2.util.MimeTypes;
import com.openiptv.code.htsp.HTSPMessage;

public class VorbisReader extends AudioReader {
    public VorbisReader(Context context) {
        super(context, MimeTypes.AUDIO_VORBIS);
    }

    @Override
    protected void buildInitializationData(HTSPMessage message) {
        if (message.containsKey("meta")) {
            Log.e(TAG, "VORBIS Not Supported Yet!");
        }
    }
}
