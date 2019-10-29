package ta.na.mao.utils.charts;


import ta.na.mao.R;
import ta.na.mao.utils.Utils;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;


import android.widget.TextView;
import com.github.mikephil.charting.components.MarkerView;
import android.content.Context;

public class MyMarkerView extends MarkerView {

    private TextView tvContent;

    public MyMarkerView (Context context, int layoutResource) {
        super(context, layoutResource);
        // this markerview only displays a textview
        tvContent = (TextView) findViewById(R.id.tvContent);
    }

    // callbacks everytime the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        tvContent.setText("R$ " + Utils.formatingToCurrency(((Float)e.getY()).doubleValue() ) + " "); // set the entry-value as the display text
        super.refreshContent(e, highlight);

    }

}