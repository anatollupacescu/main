 package net.easymock;
 
 import java.math.BigDecimal;
 
 public interface DataAccess {
     BigDecimal getPriceBySku(String sku);
 }
