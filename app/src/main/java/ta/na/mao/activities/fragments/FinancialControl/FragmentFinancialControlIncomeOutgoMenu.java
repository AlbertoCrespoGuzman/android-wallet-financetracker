package ta.na.mao.activities.fragments.FinancialControl;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;

import java.util.Calendar;
import java.util.Date;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import ta.na.mao.R;
import ta.na.mao.activities.FinancialControlMainActivity;
import ta.na.mao.utils.Defaultdata;
import ta.na.mao.utils.Utils;

import static ta.na.mao.utils.Utils.getTotalBalance;

public class FragmentFinancialControlIncomeOutgoMenu extends Fragment {

    View view;
    Button income, outgo, showStatement;
    PieChart chart;
    TextView balanceText;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final LayoutInflater inflaterFinal = inflater;
        final ViewGroup containerFinal = container;




      if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            view = inflater.inflate(R.layout.fragment_financialcontrol_incomeoutcome_menu, container, false);
        }else{
            view = inflater.inflate(R.layout.fragment_financialcontrol_incomeoutcome_menu_old, container, false);
        }

        income = view.findViewById(R.id.fragment_financialcontrol_incomeoutgo_menu_income_button);
        outgo = view.findViewById(R.id.fragment_financialcontrol_incomeoutgo_menu_outgo_button);
        chart = view.findViewById(R.id.fragment_financialcontrol_incomeoutgo_menu_chart);
        balanceText = view.findViewById(R.id.fragment_financialcontrol_incomeoutgo_menu_balance);
        showStatement = view.findViewById(R.id.fragment_financialcontrol_incomeoutgo_menu_show_statement);
        showStatement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                c.setTime(new Date());
                ((FinancialControlMainActivity) getActivity()).changeToFragmentStatement(c.get(Calendar.MONTH) + 1,
                        c.get(Calendar.YEAR));
            }
        });


        balanceText.setText("R$ " + Utils.formatingToCurrency(getTotalBalance(getActivity())));
        Utils.loadPieChartBalance(chart, getActivity(),
                Utils.getTotalIncomeOutgo(getActivity(), true),
                Utils.getTotalIncomeOutgo(getActivity(), false));


        income.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((FinancialControlMainActivity)getActivity()).changeToFragmentIncomeOutGoForm(true);
            }
        });
        outgo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((FinancialControlMainActivity)getActivity()).changeToFragmentIncomeOutGoForm(false);
            }
        });
        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
        if(getActivity() instanceof FinancialControlMainActivity){
            if(getActivity() != null){
                ((FinancialControlMainActivity) getActivity()).changeTopBarTitle(Defaultdata.TopBarTitle.INCOME_OUTGO);
            }
        }
    }



}