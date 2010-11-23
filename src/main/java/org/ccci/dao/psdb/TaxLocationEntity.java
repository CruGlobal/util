package org.ccci.dao.psdb;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.ccci.annotations.JpaConstructor;
import org.ccci.util.ValueObject;
import org.jboss.seam.log.Log;
import org.jboss.seam.log.Logging;


/**
 * To calculate overtime, we shouldn't be using someone's state of residence 
 * (ie. ps_employees.state). Instead, we should look up their tax location in 
 * (via tax_location_cd) in tax_location1 to get their state.
 * 
 * @author Linda Ye
 */

@Entity
@Table(name="ps_tax_location1")
public class TaxLocationEntity implements Serializable
{
		private static final long serialVersionUID = 1L;
			        
	    
		@Embeddable
	    public static class Key extends ValueObject implements Serializable
	    {
	        private static final long serialVersionUID = 1L;
	        
	        public Key(String taxCd, String effStatus)
	        {
	            this.taxLocationCd = taxCd;
	            this.effStatus = effStatus;
	        }
	
	        @JpaConstructor
	        protected Key()
	        {
	        }
	
	        private static Log log = Logging.getLog(Key.class);
	
	        
	        @Column(name = "TAX_LOCATION_CD", nullable = false)
		    public String taxLocationCd;  
		    
		    @Column(name="eff_status", nullable = false)
		    public String effStatus; 
		    
		    
	        @Override
	        protected Object[] getComponents()
	        {
	            return new Object[]{taxLocationCd, effStatus};
	        }
	        
	        public String getTaxLocationCd()
		    {
		        return taxLocationCd;
		    }
		    
		    public String getEffStatus()
		    {
		        return effStatus;
		    }
	       
	    }
		
		@EmbeddedId
	    private Key key = new Key();
		
		public Key getKey()
		{
			return key;
		}
	          
	    @Column(name="state", nullable = false)
	    private String state;
	
		public String getState() {
			return state;
		}
		
	}



