package ta.na.mao.database.manager;


import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import ta.na.mao.database.models.Blocker;
import ta.na.mao.database.models.Goal;
import ta.na.mao.database.models.Installment;
import ta.na.mao.database.models.Task;
import ta.na.mao.database.models.Transaction;
import ta.na.mao.database.models.User;
import ta.na.mao.database.models.UserDetails;
import ta.na.mao.database.models.productprice.FixedVariableCost;
import ta.na.mao.database.models.productprice.ProductPrice;
import ta.na.mao.database.models.productprice.Raw;
import ta.na.mao.database.models.serviceprice.FixedCost;
import ta.na.mao.database.models.serviceprice.LabourCost;
import ta.na.mao.database.models.serviceprice.LabourTax;
import ta.na.mao.database.models.serviceprice.ServicePrice;
import ta.na.mao.database.models.serviceprice.VariableCost;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;



public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME = "tanamao.db";
    private static final int DATABASE_VERSION = 8;

    private Dao<User, Long> userDao = null;
    private Dao<UserDetails, Long> userDetailsDao = null;
    private Dao<Transaction, Long> transactionDao = null;
    private Dao<Installment, Long> installmentDao = null;
    private Dao<Goal, Long> goalDao = null;
    private Dao<Task, Long> taskDao = null;
    private Dao<Blocker, Long> blockerDao = null;
    private Dao<ServicePrice, Long> servicepriceDao = null;
    private Dao<LabourCost, Long> labourcostDao = null;
    private Dao<FixedCost, Long> fixedCostDao = null;
    private Dao<LabourTax, Long> labourTaxDao = null;
    private Dao<VariableCost, Long> variableCostDao = null;
    private Dao<ProductPrice, Long> productPriceDao = null;
    private Dao<Raw, Long> rawDao = null;
    private Dao<FixedVariableCost, Long> fixedVariableCostDao  = null;
    /**
     *
     * @param context
     */
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     *
     */

    public void onCreate(SQLiteDatabase database,
                         ConnectionSource connectionSource) {
        try {

            TableUtils.createTableIfNotExists(connectionSource, User.class );
            TableUtils.createTableIfNotExists(connectionSource, UserDetails.class );
            TableUtils.createTableIfNotExists(connectionSource, Transaction.class );
            TableUtils.createTableIfNotExists(connectionSource, Installment.class );
            TableUtils.createTableIfNotExists(connectionSource, Goal.class );
            TableUtils.createTableIfNotExists(connectionSource, Task.class );
            TableUtils.createTableIfNotExists(connectionSource, Blocker.class );
            TableUtils.createTableIfNotExists(connectionSource, ServicePrice.class );
            TableUtils.createTableIfNotExists(connectionSource, LabourCost.class );
            TableUtils.createTableIfNotExists(connectionSource, FixedCost.class );
            TableUtils.createTableIfNotExists(connectionSource, LabourTax.class );
            TableUtils.createTableIfNotExists(connectionSource, VariableCost.class );
            TableUtils.createTableIfNotExists(connectionSource, ProductPrice.class );
            TableUtils.createTableIfNotExists(connectionSource, Raw.class );
            TableUtils.createTableIfNotExists(connectionSource, FixedVariableCost.class );


        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Can't create database!!!!!!!!!", e);
            throw new RuntimeException(e);
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }

    /**
     *
     */

    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource,
                          int oldVersion, int newVersion) {
        try {
            Log.i(DatabaseHelper.class.getName(), "onUpgrade");
              try {
                TableUtils.dropTable(connectionSource, User.class, true);
                TableUtils.dropTable(connectionSource, Task.class, true);
         /*     TableUtils.dropTable(connectionSource, UserDetails.class, true);
              TableUtils.dropTable(connectionSource, Transaction.class, true);
              TableUtils.dropTable(connectionSource, Installment.class, true);
              TableUtils.dropTable(connectionSource, Goal.class, true);
              TableUtils.dropTable(connectionSource, Task.class, true);
              TableUtils.dropTable(connectionSource, Blocker.class, true);
              TableUtils.dropTable(connectionSource, ServicePrice.class, true);
              TableUtils.dropTable(connectionSource, LabourCost.class, true);
              TableUtils.dropTable(connectionSource, FixedCost.class, true);
              TableUtils.dropTable(connectionSource, LabourTax.class, true);
              TableUtils.dropTable(connectionSource, VariableCost.class, true);
              */

            } catch (java.sql.SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            // after we drop the old databases, we create the new ones
            onCreate(db, connectionSource);
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Can't drop databases", e);
            throw new RuntimeException(e);
        }

    }

    /**
     *
     * @return
     */

    public Dao<User, Long> getUserDao() {
        if (null == userDao) {
            try {
                userDao = getDao(User.class);
            } catch (java.sql.SQLException e) {
                e.printStackTrace();
            }
        }
        return userDao;
    }
    public Dao<UserDetails, Long> getUserDetailsDao() {
        if (null == userDetailsDao) {
            try {
                userDetailsDao = getDao(UserDetails.class);
            } catch (java.sql.SQLException e) {
                e.printStackTrace();
            }
        }
        return userDetailsDao;
    }
    public Dao<Transaction, Long> getTransactionDao() {
        if (null == transactionDao) {
            try {
                transactionDao = getDao(Transaction.class);
            } catch (java.sql.SQLException e) {
                e.printStackTrace();
            }
        }
        return transactionDao;
    }
    public Dao<Installment, Long> getInstallmentDao() {
        if (null == installmentDao) {
            try {
                installmentDao = getDao(Installment.class);
            } catch (java.sql.SQLException e) {
                e.printStackTrace();
            }
        }
        return installmentDao;
    }
    public Dao<Goal, Long> getGoalDao() {
        if (null == goalDao) {
            try {
                goalDao = getDao(Goal.class);
            } catch (java.sql.SQLException e) {
                e.printStackTrace();
            }
        }
        return goalDao;
    }
    public Dao<Task, Long> getTaskDao() {
        if (null == taskDao) {
            try {
                taskDao = getDao(Task.class);
            } catch (java.sql.SQLException e) {
                e.printStackTrace();
            }
        }
        return taskDao;
    }
    public Dao<Blocker, Long> getBlockerDao() {
        if (null == blockerDao) {
            try {
                blockerDao = getDao(Blocker.class);
            } catch (java.sql.SQLException e) {
                e.printStackTrace();
            }
        }
        return blockerDao;
    }
    public Dao<ServicePrice, Long> getServicepriceDao() {
        if (null == servicepriceDao) {
            try {
                servicepriceDao = getDao(ServicePrice.class);
            } catch (java.sql.SQLException e) {
                e.printStackTrace();
            }
        }
        return servicepriceDao;
    }
    public Dao<LabourCost, Long> getLabourcostDao() {
        if (null == labourcostDao) {
            try {
                labourcostDao = getDao(LabourCost.class);
            } catch (java.sql.SQLException e) {
                e.printStackTrace();
            }
        }
        return labourcostDao;
    }
    public Dao<FixedCost, Long> getFixedCostDao() {
        if (null == fixedCostDao) {
            try {
                fixedCostDao = getDao(FixedCost.class);
            } catch (java.sql.SQLException e) {
                e.printStackTrace();
            }
        }
        return fixedCostDao;
    }
    public Dao<LabourTax, Long> getLabourTaxDao() {
        if (null == labourTaxDao) {
            try {
                labourTaxDao = getDao(LabourTax.class);
            } catch (java.sql.SQLException e) {
                e.printStackTrace();
            }
        }
        return labourTaxDao;
    }
    public Dao<VariableCost, Long> getVariableCostDao() {
        if (null == variableCostDao) {
            try {
                variableCostDao = getDao(VariableCost.class);
            } catch (java.sql.SQLException e) {
                e.printStackTrace();
            }
        }
        return variableCostDao;
    }
    public Dao<ProductPrice, Long> getProductPriceDao() {
        if (null == productPriceDao) {
            try {
                productPriceDao = getDao(ProductPrice.class);
            } catch (java.sql.SQLException e) {
                e.printStackTrace();
            }
        }
        return productPriceDao;
    }
    public Dao<Raw, Long> getRawDao() {
        if (null == rawDao) {
            try {
                rawDao = getDao(Raw.class);
            } catch (java.sql.SQLException e) {
                e.printStackTrace();
            }
        }
        return rawDao;
    }
    public Dao<FixedVariableCost, Long> getFixedVariableCostDao() {
        if (null == fixedVariableCostDao) {
            try {
                fixedVariableCostDao = getDao(FixedVariableCost.class);
            } catch (java.sql.SQLException e) {
                e.printStackTrace();
            }
        }
        return fixedVariableCostDao;
    }
    @Override
    public void close() {
        super.close();
        userDao = null;
        userDetailsDao = null;
        transactionDao = null;
        installmentDao = null;
        goalDao = null;
        taskDao = null;
        blockerDao = null;
        labourcostDao = null;
        servicepriceDao = null;
        fixedCostDao = null;
        labourTaxDao = null;
        variableCostDao = null;
        productPriceDao = null;
        rawDao = null;
        fixedVariableCostDao = null;
    }


}

