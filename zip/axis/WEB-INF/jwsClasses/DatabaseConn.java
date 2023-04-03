import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

public class DatabaseConn {
   private Connection con = null;
   private Statement sta;

   public DatabaseConn(String var1, String var2, String var3, String var4) {
      try {
         Class.forName("org.postgresql.Driver");
         System.out.println("Server: " + var1);
         this.con = DriverManager.getConnection("jdbc:postgresql://" + var1 + ":5432/" + var2, var3, var4);
         if (this.con != null) {
            System.out.println("......Succesfully Connected To DB......");
         } else {
            System.out.println("Unable to connect");
         }
      } catch (Exception var6) {
         System.out.println(var6.getMessage());
      }

   }

   public void closeConn() {
      try {
         this.con.close();
      } catch (SQLException var2) {
         var2.printStackTrace();
      }

   }

   public Vector executeQueryToVector(String var1) throws Exception {
      Vector var2 = new Vector();
      int var5 = 0;
      ResultSet var7 = null;

      try {
         var7 = this.executeQuery(var1);
         int var6 = var7.getMetaData().getColumnCount();

         while(var7.next()) {
            Vector var3 = new Vector();

            for(int var4 = 1; var4 <= var6; ++var4) {
               if (var7.getString(var4) != null && var7.getString(var4) != "") {
                  var3.add(var4 - 1, var7.getString(var4));
               } else {
                  var3.add(var4 - 1, " ");
               }
            }

            var2.add(var5++, var3);
         }
      } catch (Exception var12) {
         throw var12;
      } finally {
         if (var7 != null) {
            var7.close();
         }

         var7 = null;
      }

      return var2;
   }

   public ResultSet executeQuery(String var1) throws SQLException {
      try {
         this.sta = this.con.createStatement();
         ResultSet var2 = this.sta.executeQuery(var1);
         return var2;
      } catch (Exception var4) {
         var4.printStackTrace();
         throw var4;
      }
   }
}
