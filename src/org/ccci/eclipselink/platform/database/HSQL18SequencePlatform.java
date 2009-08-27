package org.ccci.eclipselink.platform.database;

import java.io.IOException;
import java.io.Writer;

import org.eclipse.persistence.platform.database.HSQLPlatform;
import org.eclipse.persistence.queries.ValueReadQuery;


/**
 * Adapted from https://glassfish.dev.java.net/issues/show_bug.cgi?id=3623, user magott
 * @author Matt Drees
 *
 */
public class HSQL18SequencePlatform extends HSQLPlatform
{
    private static final long serialVersionUID = 1L;

    /**
     * INTERNAL:
     * HSQL 1.8 supports both Sequences and Identity columns
     */
    @Override
    public boolean supportsNativeSequenceNumbers() {
        return true;
    }   
    
    @Override
    public boolean supportsSequenceObjects()
    {
        return true;
    }

    
    
    /**
     * Taken from the HSQL Documentation:
     * 
     * Please note that the semantics of sequences is not exactly the same as defined by SQL 200n. 
     * For example if you use the same sequence twice in the same row insert query, 
     * you will get two different values, not the same value as required by the standard.
     * 
     * This should however not affect the usage in JPA
     */
    @Override
    public ValueReadQuery buildSelectQueryForSequenceObject(String seqName, Integer size) {
        return new ValueReadQuery("SELECT NEXT VALUE FOR " + seqName + " FROM INFORMATION_SCHEMA.SYSTEM_SEQUENCES");
    }
    
    @Override
    public Writer buildSequenceObjectCreationWriter(Writer writer, String fullSeqName, int increment, int start)
            throws IOException
    {
        writer.write("CREATE SEQUENCE ");
        writer.write(fullSeqName);
        writer.write(" START WITH " + start);            
        if (increment != 1) {
            writer.write(" INCREMENT BY " + increment);
        }
        return writer;
    }
    
    @Override
    public Writer buildSequenceObjectDeletionWriter(Writer writer, String fullSeqName) throws IOException
    {
        writer.write("DROP SEQUENCE ");
        writer.write(fullSeqName);
        return writer;
    }
    
}
