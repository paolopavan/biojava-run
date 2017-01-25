package org.biojava.nbio.run.blastplus;

import org.biojava.nbio.core.sequence.io.FastaWriterHelper;
import org.biojava.nbio.core.sequence.template.AbstractSequence;
import java.util.ArrayList;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.biojava.nbio.core.sequence.DNASequence;
import org.biojava.nbio.core.sequence.compound.AmbiguityDNACompoundSet;
import org.biojava.nbio.core.sequence.compound.NucleotideCompound;
import org.biojava.nbio.core.sequence.io.FastaReader;
import org.biojava.nbio.core.sequence.io.FastaSequenceParser;
import org.biojava.nbio.core.sequence.io.FastaWriter;
import org.biojava.nbio.core.sequence.io.FileProxyDNASequenceCreator;
import org.biojava.nbio.core.sequence.io.GenericFastaHeaderFormat;
import org.biojava.nbio.core.sequence.io.GenericFastaHeaderParser;
import org.biojava.nbio.core.sequence.template.Compound;
import org.biojava.nbio.core.search.io.blast.BlastXMLParser;
import org.biojava.nbio.core.search.io.Result;
import org.biojava.nbio.core.search.io.ResultFactory;
import org.biojava.nbio.core.search.io.SearchIO;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Iterator;
import org.biojava.nbio.core.sequence.template.Sequence;



public class BlastPlus implements Iterable<Result> {
    final private boolean DEBUG = false;
    private final String baseResourcePath = "/org/biojava/nbio/run/blastplus/";
    public enum Task {
        REGULAR, MEGABLAST, DISCONTINUOUS_MEGABLAST, SHORT
    }

    public enum Program {
        BLASTN, BLASTP, BLASTX, TBLASTN, TBLASTX
    }

    /**
     * database fasta file of sequences
     */
    private File database;
    /**
     * path of database index files
     */
    private File indexPath;
    /**
     * path of database index files
     */
    private File queryFile;
    List<AbstractSequence> querySequences = null;
    List<AbstractSequence> databaseSequences = null;
    /**
     * internally used to iterate results. 
     * Since this module is a wrapper around a program, this property is designed
     * to be setted by a SearchIO reader built on the program output file.
     */
    private Iterator<Result> resultsIterator;
    
    private Program program;
    private Task task = Task.REGULAR;
    private static OsCheck.OSType OS = OsCheck.getOperatingSystemType();
    private static String DM = OsCheck.getArchDataModel();
    private ArrayList<String> optionsList = new ArrayList();
    private int threads = Runtime.getRuntime().availableProcessors();

    public BlastPlus() {

    }

    public void run() throws IOException, ParseException, IllegalStateException, Exception { // TODO: modify Exception
        if (queryFile == null || queryFile.length() == 0) throw new IllegalStateException("Query sequences not specified.");
        if (database.length()==0) throw new IllegalStateException("Database sequences not specified.");
        
        File outputFile = File.createTempFile("output","tmp");
        List<String> commandList = new ArrayList();
        commandList.add(getProgramExecutable().getCanonicalPath().replaceAll("%20"," "));
        
        commandList.add("-outfmt");
        commandList.add("5");
        
        commandList.add("-db");
        commandList.add(database.getAbsolutePath().replaceAll("%20"," "));
        
        commandList.add("-num_threads");
        commandList.add(new Integer(threads).toString());
        
        commandList.add("-out");
        commandList.add(outputFile.getCanonicalPath().replaceAll("%20"," "));
        
        commandList.add("-query");
        commandList.add(queryFile.getCanonicalPath().replaceAll("%20"," "));
        
        commandList.addAll(optionsList);

        switch (task) {
            case REGULAR:
                break;
            case MEGABLAST:
                if (program == Program.BLASTN || program == Program.BLASTP){
                    commandList.add("-task");
                    commandList.add("megablast");
                }
                break;
            case DISCONTINUOUS_MEGABLAST:
                if (program == Program.BLASTN || program == Program.BLASTP){
                    commandList.add("-task");
                    commandList.add("dc-megablast");
                }
                break;
            case SHORT:
                if (program == Program.BLASTN) {
                    commandList.add("-task");
                    commandList.add("blastn-short");
                } else {
                    commandList.add("-task");
                    commandList.add("blastp-short");
                }
                break;
            default:
                // default is regular, will not throw any exception
                break;
        }
        
        ProcessBuilder pb = new ProcessBuilder(commandList);        
        Process p = pb.start();
        
        InputStream stdout = p.getInputStream ();
        
        // String o contains output from stdout of the program
        // I read it just for debug purposes
        java.util.Scanner s = new java.util.Scanner(stdout).useDelimiter("\\n");
        String o = s.hasNext() ? s.next() : "";
        
        ResultFactory f = new BlastXMLParser();
        f.setQueryReferences(querySequences);
        f.setDatabaseReferences(databaseSequences);
        
        SearchIO reader = new SearchIO(outputFile, f);
        resultsIterator = reader.iterator();
    }

    public void setDatabase(String path, String baseName) {
        database = new File(path+"/"+baseName);
        indexPath = new File(path);
        //launch formatdb
        formatDatabase();
    }
    
    public void setDatabase(File f) {
        database = f;
        indexPath = database.getParentFile();
        //launch formatdb
        formatDatabase();
    }

    public void setDatabase(List sequences) throws Exception {
        databaseSequences = sequences;
        database = File.createTempFile("database", ".fasta");
        if (!DEBUG) database.deleteOnExit();
        indexPath = database.getParentFile();
        
        if (
                program == Program.BLASTN || 
                program == Program.TBLASTN ||
                program == Program.TBLASTX) {
            writeFasta(database, sequences);
        } else {
            FastaWriterHelper.writeProteinSequence(database, sequences);
        }
        //launch formatdb
        formatDatabase();
    }
    
    public void setDatabase(Sequence ... sequences) throws Exception{
        setDatabase(new ArrayList(Arrays.asList(sequences)));
    }

    public void setQuery(List sequences) throws Exception {
        queryFile = File.createTempFile("query", ".fasta");
        if (!DEBUG) queryFile.deleteOnExit();
        if (
                program == Program.BLASTN || 
                program == Program.BLASTX ||
                program == Program.TBLASTX) {
            FastaWriterHelper.writeNucleotideSequence(queryFile, sequences);
        } else {
            FastaWriterHelper.writeProteinSequence(queryFile, sequences);
        }
        querySequences = sequences;
    }
    
    public void setQuery(Sequence ... sequences ) throws Exception{
        setQuery(new ArrayList(Arrays.asList(sequences)));
    }

    public void setTask(Task t) {
        task = t;
    }

    public void setProgram(Program p) {
        program = p;
    }

    public void setNumThreads(int n) {
        threads = n;
    }

    public void setNumAlignments(int n) {
    }

    public void passProgramOption(String option, String value) {
        if (!option.startsWith("-")) {
            option = "-" + option;
        }
        optionsList.add(option);
        optionsList.add(value);
    }

    public String getVersion() throws IOException {
        File exe = getProgramExecutable();
        
        List<String> commandString = new ArrayList();
        
        // replace is a needed workaround
        commandString.add(exe.getAbsolutePath().replaceAll("%20"," "));
        commandString.add("-version");
        
        ProcessBuilder pb = new ProcessBuilder(commandString);        
        Process p = pb.start();
        
        InputStream stdout = p.getInputStream ();
        
        //useDelimiter("\\A") to scan all the version output
        java.util.Scanner s = new java.util.Scanner(stdout).useDelimiter("\\n");
        
        return s.hasNext() ? s.next() : "";
    }

    private File getProgramExecutable() {
        String resource = baseResourcePath;
        resource += OS + DM + "/";
        switch (program) {
            case BLASTP:
                resource += "blastp";
                break;
            case BLASTN:
                resource += "blastn";
                break;
            case BLASTX:
                resource += "blastx";
                break;
            case TBLASTN:
                resource += "tblastn";
                break;
            case TBLASTX:
                resource += "blastn";
                break;
            default:
                throw new RuntimeException("Blast program not declared (blastn, blastp or so)");
        }

        return unpackResourceFile(resource);
    }

    private File unpackResourceFile(String resource) {
        /**
         * source by tombart, adapted *
         * http://stackoverflow.com/questions/941754/how-to-get-a-path-to-a-resource-in-a-java-jar-file
		*
         */
        File file = null;
        URL res = getClass().getResource(resource);
        if (res == null) {
            throw new RuntimeException("OS " + OS + " is not supported");
        }
        if (res.toString().startsWith("jar:")) {
            try {
                InputStream input = getClass().getResourceAsStream(resource);
                file = File.createTempFile("javawrapped", ".exe");
                if (!DEBUG) file.deleteOnExit();
                file.setExecutable(true, true);
                OutputStream out = new FileOutputStream(file);
                int read;
                byte[] bytes = new byte[1024];

                while ((read = input.read(bytes)) != -1) {
                    out.write(bytes, 0, read);
                }
                out.close();
                out.flush();
            } catch (IOException ex) {
                throw new RuntimeException("Resource not available for this OS!");
            } 
        } else {
            //this will probably work in your IDE, but not from a JAR
            file = new File(res.getFile());
        }

        if (file == null) {
            throw new RuntimeException("Error: File " + file + " not found!");
        }
        return file;
    }
    
    /**
     * not to be called directly, use setDatabase() instead
     */
    private void formatDatabase() {
        String resource = baseResourcePath;
        resource += OS+ DM + "/makeblastdb";
        File exe = unpackResourceFile(resource);

        List<String> commandString = new ArrayList();
        try {
            // replace is a needed workaround
            commandString.add(exe.getAbsolutePath().replaceAll("%20"," "));
            commandString.add("-in");
            commandString.add(database.getAbsolutePath().replaceAll("%20"," "));
            commandString.add("-out");
            commandString.add((indexPath.getPath() + "//"+ database.getName()).replaceAll("%20"," "));
            commandString.add("-dbtype");
            
            if (program == null) throw new RuntimeException("Blast program (blastn/blastp...) not declared.");
            switch (program) {
                case BLASTP:
                    commandString.add("prot");
                    break;
                case BLASTN : 
                    commandString.add("nucl");
                    break;
                case TBLASTN:
                    commandString.add("nucl");
                    break;
                case TBLASTX:
                    commandString.add("nucl");
                    break;
                case BLASTX:
                    commandString.add("prot");
                    break;
            }
            
            ProcessBuilder pb = new ProcessBuilder(commandString);
            pb.redirectErrorStream(true);
            Process p = pb.start();
            
            InputStream stdout = p.getInputStream ();
            
            int result;
            try {
                result = p.waitFor();
            } catch (InterruptedException ex) {
                Logger.getLogger(BlastPlus.class.getName()).log(Level.SEVERE, null, ex);
                throw new RuntimeException("Program interrupted");
            }
            
            // outputstream needed only for debug purposes
            java.util.Scanner s = new java.util.Scanner(stdout).useDelimiter("\\A");
            String o = s.hasNext() ? s.next() : "";
            
            // check database index files creation and process result
            String baseName = (indexPath.getPath() + "//"+ database.getName()).replaceAll("%20"," ");
            File nhr = new File(baseName+".nhr");
            File nin = new File(baseName+".nin");
            File nsq = new File(baseName+".nsq");
            
            if (!DEBUG) {
                nhr.deleteOnExit();
                nin.deleteOnExit();
                nsq.deleteOnExit();
            }
            
            if (!nhr.exists() || !nin.exists()||!nsq.exists() || result != 0) {
                throw new RuntimeException("Error: cannot write database indexes");
            }
        } catch (IOException e) {
            throw new RuntimeException("Error: unexpected IOException\n"+e.getMessage());
        }
    }
    
    private static LinkedHashMap<String, DNASequence> readFasta(File f) throws FileNotFoundException, IOException {
        LinkedHashMap<String, DNASequence> ss = new LinkedHashMap<>();

        FastaReader<DNASequence, NucleotideCompound> fastaProxyReader
                = new FastaReader<DNASequence, NucleotideCompound>(
                        f,
                        new GenericFastaHeaderParser<DNASequence, NucleotideCompound>(),
                        new FileProxyDNASequenceCreator(f,
                                new AmbiguityDNACompoundSet(),
                                new FastaSequenceParser()
                        )
                );
        
        ss = fastaProxyReader.process();

        return ss;
    }
    
    private static void writeFasta(File f, List sequences) throws Exception{
        FileOutputStream os = new FileOutputStream(f);
        FastaWriter<DNASequence, Compound> fastaProxyWriter
                = new FastaWriter<DNASequence, Compound>(
                        os,
                        sequences,
                        new GenericFastaHeaderFormat<DNASequence, Compound>()
                );
        
        fastaProxyWriter.process();
        os.flush();
        os.getFD().sync();
    }
    
    @Override
    public Iterator<Result> iterator() {
        return new Iterator<Result> () {

            @Override
            public boolean hasNext() {
                return resultsIterator.hasNext();
            }

            @Override
            public Result next() {
                return resultsIterator.next();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("remove() operation not supported.");
            }
        };
    }
    
    
/**
 * just for test purposes. must be removed
 * @param args
 * @throws Exception 
 */
    public static void main(String[] args) throws Exception{
        BlastPlus searchEngine = new BlastPlus();
        searchEngine.setProgram(Program.BLASTN);
        System.out.println("Blast version: " +searchEngine.getVersion()+"-");
        
        File queryFile,databaseFile;
        if (args.length == 0) {
            databaseFile = new File("16SMicrobial.fasta");
            queryFile = new File("less_queries.fasta");
        } else {
            databaseFile = new File(args[0]);
            queryFile = new File(args[1]);
        }
        
        ArrayList sequences;
        if (false) {
            searchEngine.setDatabase(databaseFile);
        } else {
            LinkedHashMap<String, DNASequence> databaseSeqs = readFasta(databaseFile);

            sequences = new ArrayList();
            for (DNASequence s: databaseSeqs.values()) {
                sequences.add(s);
            }
            databaseSeqs = null;
            searchEngine.setDatabase(sequences);
        }
        
        LinkedHashMap<String, DNASequence> querySeqs = readFasta(queryFile);
        
        sequences = new ArrayList();
        for (DNASequence s: querySeqs.values()) {
            sequences.add(s);
        }
        querySeqs=null;
        searchEngine.setQuery(sequences);
        
        searchEngine.run();
    }
}

/**
 * helper class to check the operating system this Java VM runs in
 */
final class OsCheck {

    /**
     * types of Operating Systems
     */
    public enum OSType {

        Windows, MacOS, Linux, Other
    }

    protected static OSType detectedOS;
    protected final static String archDataModel = System.getProperty("sun.arch.data.model");

    /**
     * detected the operating system from the os.name System property and cache
     * the result
     *     
     * @returns - the operating system detected
     */
    public static OSType getOperatingSystemType() {
        if (detectedOS == null) {
            String OS = System.getProperty("os.name", "generic").toLowerCase();
            if (OS.indexOf("win") >= 0) {
                detectedOS = OSType.Windows;
            } else if ((OS.indexOf("mac") >= 0) || (OS.indexOf("darwin") >= 0)) {
                detectedOS = OSType.MacOS;
            } else if (OS.indexOf("nux") >= 0) {
                detectedOS = OSType.Linux;
            } else {
                detectedOS = OSType.Other;
            }
        }
        return detectedOS;
    }
    public static String getArchDataModel(){
        return archDataModel;
    }
}
