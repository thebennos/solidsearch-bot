package de.solidsearch.bot.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import com.vaadin.server.DownloadStream;
import com.vaadin.server.FileResource;

public class FileResourceUtil
{

    public FileResource createFileResource(File file) 
    {
        return new FileResource(file) 
        {
			private static final long serialVersionUID = -4183674760353232866L;

			@Override
            public DownloadStream getStream() 
            {
                try 
                {
                    final DownloadStream ds = new DownloadStream(new FileInputStream(getSourceFile()), getMIMEType(), getFilename());
                    ds.setParameter("Content-Length", String.valueOf(getSourceFile().length()));
                    ds.setCacheTime(getCacheTime());
                    
                    return ds;
                } 
                catch (final FileNotFoundException e) 
                {
                    return null;
                }//end try-catch block...                
            }//end method...
        };        
    }//end method...

}
