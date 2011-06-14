package org.ccci.hibernate;

import org.hibernate.AssertionFailure;
import org.hibernate.annotations.common.util.StringHelper;
import org.hibernate.cfg.ImprovedNamingStrategy;

public class UnderscoreNamingStrategy extends ImprovedNamingStrategy {
	private static final long serialVersionUID = 1L;

	@Override
	public String classToTableName(String className) {
		return super.classToTableName(className);
	}
	
	@Override
	public String collectionTableName(String ownerEntity, String ownerEntityTable, String associatedEntity, 
	        String associatedEntityTable, String propertyName) {
		return super.collectionTableName(ownerEntity, ownerEntityTable,
				associatedEntity, associatedEntityTable, propertyName);
	}
	
	/**
	 * Don't *always* add underscores, because we want annotated names to remain as they are
	 */
	@Override
	public String tableName(String tableName) {
		return tableName;
	}
	
	/**
	 * Don't *always* add underscores, because we want annotated names to remain as they are
	 */
	@Override
	public String columnName(String columnName) {
		return columnName;
	}
	
	/**
	 * ImprovedNamingStrategy doesn't attach _id to the end of foreign key columns.  Not sure why.  So attach it.
	 */
	@Override
	public String foreignKeyColumnName(String propertyName, String propertyEntityName, String propertyTableName, 
			String referencedColumnName) {
		String header = propertyName != null ? StringHelper.unqualify( propertyName ) : propertyTableName;
		if (header == null) {
            throw new AssertionFailure("NamingStrategy not properly filled");
        }
		return addUnderscores(header) + "_" + referencedColumnName;
	}
}
