package ta.na.mao.utils.adapters;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Pattern;

import androidx.appcompat.widget.ListPopupWindow;
import androidx.recyclerview.widget.RecyclerView;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import ta.na.mao.R;
import ta.na.mao.activities.FinancialControlMainActivity;
import ta.na.mao.database.manager.DatabaseManager;
import ta.na.mao.database.models.Goal;
import ta.na.mao.utils.Utils;
import ta.na.mao.utils.dialogs.GoalFormDialog;

import static ta.na.mao.utils.Utils.measureContentHeight;
import static ta.na.mao.utils.Utils.measureContentWidth;

public class GoalsRecyclerAdapter extends RecyclerView.Adapter<GoalsRecyclerAdapter.ViewHolder> {

    private List<Goal> goals = new ArrayList<>();
    private LayoutInflater mInflater;
    private StatementRecyclerAdapter.ItemClickListener mClickListener;
    SimpleDateFormat dateFormat, dateFormat1;
    Activity context;
    ViewGroup parent;
    DecimalFormat newFormat = new DecimalFormat("###,###,##0.00");
    DatabaseManager db;


    public GoalsRecyclerAdapter(Activity context, List<Goal> goals) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        dateFormat = new SimpleDateFormat("dd MMMM");
        db = new DatabaseManager(context);
        this.goals = goals;
        dateFormat1 = new SimpleDateFormat("dd/MM/yyyy");

    }

    @Override
    public GoalsRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.parent = parent;
        View view = mInflater.inflate(R.layout.adapter_goal_content_element, parent, false);
        return new GoalsRecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GoalsRecyclerAdapter.ViewHolder holder, int position) {
        final Goal goal = goals.get(position);
        Log.e("onBindViewHolder","onBindViewHolder ADAPTER puto goal-> " + goal.toString());
        final List<Goal> goalsfinal = goals;
        final int positionfinal = position;

        final GoalsRecyclerAdapter.ViewHolder holderfinal = holder;

        if(goal.isIncome()){
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                holderfinal.moneybag.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_money_bag_plus));
            }else{
                holderfinal.moneybag.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_money_bag_plus_png));
            }

            holderfinal.barLayout.setBackgroundColor(context.getResources().getColor(R.color.income_green));
            holderfinal.valueText.setText("+R$ ");
            holderfinal.goalType.setText(context.getResources().getString(R.string.goals_income));
        }else{
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                holderfinal.moneybag.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_money_bag_menos));
            }else{
                holderfinal.moneybag.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_money_bag_menos_png));
            }
            holderfinal.barLayout.setBackgroundColor(context.getResources().getColor(R.color.outgo_red));
            holderfinal.valueText.setText("-R$ ");
            holderfinal.goalType.setText(context.getResources().getString(R.string.goals_outgo));
        }

        holderfinal.category.setText(goal.getCategoryText(context));
        holderfinal.valueText.setText(holderfinal.valueText.getText() + newFormat.format(goal.getValue()));

        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(goal.getFirstdate());

        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(goal.getLastdate());

        holderfinal.date1.setText(Utils.getDate3LettersMonthFormatNoYear(calendar1.getTime()));
        holderfinal.date2.setText(Utils.getDate3LettersMonthFormatNoYear(calendar2.getTime()));

        String[] web = {"OPÇÕES", "Editar meta", "Deletar meta", "Fechar"};
        PopupMenuAdapter popupMenuAdapter = new PopupMenuAdapter(context, R.layout.menu_popup_element, web);

        holderfinal.optionsMenu = new ListPopupWindow(context);
        holderfinal.optionsMenu.setAdapter(popupMenuAdapter);
        holderfinal.optionsMenu.setAnchorView(holderfinal.options);
        holderfinal.optionsMenu.setWidth(measureContentWidth(popupMenuAdapter, context));
        holderfinal.optionsMenu.setHeight(measureContentHeight(popupMenuAdapter, context));

        holderfinal.optionsMenu.setModal(true);
        holderfinal.optionsMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e("position","position " + position);
                holderfinal.optionsMenu.dismiss();
                if(position == 1){
                    String firsdate = dateFormat1.format(goals.get(positionfinal).getFirstdate());

                    GoalFormDialog goalFormDialog = new GoalFormDialog((Activity)context,
                            Integer.parseInt(firsdate.split(Pattern.quote("/"))[1]) - 1,
                            Integer.parseInt(firsdate.split(Pattern.quote("/"))[2]),
                            null, goals.get(positionfinal).getLocal_id(), goalsfinal);
                    goalFormDialog.show();

                }else if(position == 2){
                    MaterialDialog removeGoalDialog = new MaterialDialog(context);
                    removeGoalDialog.title(R.string.remove_goal_title, context.getResources().getString(R.string.remove_goal_title));
                    removeGoalDialog.message(R.string.remove_goal_dialog_message, context.getResources().getString(R.string.remove_goal_dialog_message));
                    removeGoalDialog.positiveButton(R.string.remove_goal_dialog_positive_button,
                            context.getResources().getString(R.string.remove_goal_dialog_positive_button), new Function1<MaterialDialog, Unit>() {
                                @Override
                                public Unit invoke(MaterialDialog materialDialog) {
                                    db.removeGoal(goal);
                                    String firsdate = dateFormat1.format(goal.getFirstdate());

                                    ((FinancialControlMainActivity)context).changeToFragmentGoalsMenu(Integer.parseInt(firsdate.split(Pattern.quote("/"))[1]),
                                            Integer.parseInt(firsdate.split(Pattern.quote("/"))[2]));

                                    return null;
                                }
                            });
                    removeGoalDialog.negativeButton(R.string.remove_goal_dialog_negative_button,
                            context.getResources().getString(R.string.remove_goal_dialog_negative_button), null);

                    removeGoalDialog.show();

                }
            }
        });
        holderfinal.options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holderfinal.optionsMenu.show();
            }
        });
        popupMenuAdapter.setNotifyOnChange(true);
        popupMenuAdapter.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return goals.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView moneybag ;
        RelativeLayout barLayout, options;
        TextView goalType, valueText, category, date1, date2;
        ListPopupWindow optionsMenu;

        ViewHolder(View itemView) {
            super(itemView);

            moneybag = itemView.findViewById(R.id.adapter_goal_header_moneybag);
            options = itemView.findViewById(R.id.adapter_goal_header_options);
            barLayout = itemView.findViewById(R.id.adapter_goal_header_bar_layout);
            goalType = itemView.findViewById(R.id.adapter_goal_header_income_outgo_text);
            valueText = itemView.findViewById(R.id.adapter_goal_header_value);
            category = itemView.findViewById(R.id.adapter_goal_category_text);
            date1 = itemView.findViewById(R.id.adapter_goal_date_1);
            date2 = itemView.findViewById(R.id.adapter_goal_date_2);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    public Object getItem(int id) {
        return goals.get(id);
    }

    public void setClickListener(StatementRecyclerAdapter.ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }



}