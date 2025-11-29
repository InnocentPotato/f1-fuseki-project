package com.f1.fuseki;

import org.apache.jena.query.*;
import org.apache.jena.update.UpdateExecutionFactory;
import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateRequest;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import java.io.FileInputStream;

/**
 * F1 Fuseki Manager - Client application for F1 SPARQL database
 */
public class F1FusekiManager {
    
    private static final String FUSEKI_ENDPOINT = "http://localhost:3030/f1";
    private static final String SPARQL_QUERY_URL = FUSEKI_ENDPOINT + "/sparql";
    private static final String SPARQL_UPDATE_URL = FUSEKI_ENDPOINT + "/update";
    
    public static void main(String[] args) {
        System.out.println("=========================================");
        System.out.println("    F1 Fuseki Database Manager");
        System.out.println("=========================================\n");
        
        try {
            // Check if Fuseki server is running
            if (!isServerRunning()) {
                System.out.println("❌ Fuseki server is not running!");
                System.out.println("Please start Fuseki server first using:");
                System.out.println("  scripts/start-fuseki.bat (Windows)");
                System.out.println("  scripts/start-fuseki.sh (Linux/Mac)");
                return;
            }
            
            System.out.println("✅ Connected to Fuseki server at " + FUSEKI_ENDPOINT);
            
            // Initialize database with sample data
            initializeDatabase();
            
            // Run demonstration queries
            runDemoQueries();
            
            // Show how to add new data
            demonstrateDataInsertion();
            
            System.out.println("\n🎉 F1 Fuseki Demo Completed!");
            System.out.println("Access the web interface at: http://localhost:3030");
            
        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static boolean isServerRunning() {
        try {
            String testQuery = "ASK { ?s ?p ?o }";
            Query query = QueryFactory.create(testQuery);
            try (QueryExecution qexec = QueryExecutionFactory.sparqlService(SPARQL_QUERY_URL, query)) {
                return qexec.execAsk();
            }
        } catch (Exception e) {
            return false;
        }
    }
    
    private static void initializeDatabase() {
        System.out.println("\n1. 📥 INITIALIZING DATABASE...");
        
        try {
            // First, clear any existing data
            String clearQuery = "DELETE WHERE { ?s ?p ?o }";
            UpdateRequest clearUpdate = UpdateFactory.create(clearQuery);
            UpdateExecutionFactory.createRemote(clearUpdate, SPARQL_UPDATE_URL).execute();
            
            // Load sample F1 data
            Model model = ModelFactory.createDefaultModel();
            model.read(new FileInputStream("../fuseki-server/data/f1-data.ttl"), null, "TURTLE");
            
            // Convert to SPARQL INSERT
            StringBuilder insertData = new StringBuilder();
            insertData.append("PREFIX f1: <http://f1.example.org/>\n");
            insertData.append("PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n");
            insertData.append("INSERT DATA {\n");
            
            model.listStatements().forEachRemaining(statement -> {
                insertData.append("  ");
                insertData.append(statement.getSubject().toString());
                insertData.append(" ");
                insertData.append(statement.getPredicate().toString());
                insertData.append(" ");
                
                if (statement.getObject().isLiteral()) {
                    insertData.append("\"");
                    insertData.append(statement.getObject().asLiteral().getString());
                    insertData.append("\"");
                } else {
                    insertData.append(statement.getObject().toString());
                }
                
                insertData.append(" .\n");
            });
            
            insertData.append("}");
            
            UpdateRequest update = UpdateFactory.create(insertData.toString());
            UpdateExecutionFactory.createRemote(update, SPARQL_UPDATE_URL).execute();
            
            System.out.println("✅ Database initialized with F1 data!");
            
        } catch (Exception e) {
            System.out.println("❌ Error initializing database: " + e.getMessage());
        }
    }
    
    private static void runDemoQueries() {
        System.out.println("\n2. 🔍 RUNNING DEMO QUERIES...");
        
        // Query 1: All drivers
        System.out.println("\n--- ALL F1 DRIVERS ---");
        String driversQuery = 
            "PREFIX f1: <http://f1.example.org/>\n" +
            "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
            "SELECT ?name ?number ?nationality ?teamName\n" +
            "WHERE {\n" +
            "  ?driver a f1:Driver ;\n" +
            "          rdfs:label ?name ;\n" +
            "          f1:driverNumber ?number ;\n" +
            "          f1:nationality ?nationality ;\n" +
            "          f1:team ?team .\n" +
            "  ?team rdfs:label ?teamName .\n" +
            "}\n" +
            "ORDER BY ?teamName ?name";
        
        executeQuery(driversQuery);
        
        // Query 2: World champions
        System.out.println("\n--- WORLD CHAMPIONS ---");
        String championsQuery = 
            "PREFIX f1: <http://f1.example.org/>\n" +
            "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
            "SELECT ?name ?championships ?teamName\n" +
            "WHERE {\n" +
            "  ?driver a f1:Driver ;\n" +
            "          rdfs:label ?name ;\n" +
            "          f1:worldChampionships ?championships ;\n" +
            "          f1:team ?team .\n" +
            "  ?team rdfs:label ?teamName .\n" +
            "  FILTER (?championships > \"0\")\n" +
            "}\n" +
            "ORDER BY DESC(xsd:integer(?championships))";
        
        executeQuery(championsQuery);
        
        // Query 3: Teams by championships
        System.out.println("\n--- TEAMS RANKING ---");
        String teamsQuery = 
            "PREFIX f1: <http://f1.example.org/>\n" +
            "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
            "SELECT ?teamName ?base ?championships ?principal\n" +
            "WHERE {\n" +
            "  ?team a f1:Team ;\n" +
            "        rdfs:label ?teamName ;\n" +
            "        f1:base ?base ;\n" +
            "        f1:championships ?championships ;\n" +
            "        f1:teamPrincipal ?principal .\n" +
            "}\n" +
            "ORDER BY DESC(xsd:integer(?championships))";
        
        executeQuery(teamsQuery);
        
        // Query 4: Drivers by nationality
        System.out.println("\n--- DRIVERS BY NATIONALITY ---");
        String nationalityQuery = 
            "PREFIX f1: <http://f1.example.org/>\n" +
            "SELECT ?nationality (COUNT(?driver) as ?count)\n" +
            "WHERE {\n" +
            "  ?driver a f1:Driver ;\n" +
            "          f1:nationality ?nationality .\n" +
            "}\n" +
            "GROUP BY ?nationality\n" +
            "ORDER BY DESC(?count)";
        
        executeQuery(nationalityQuery);
    }
    
    private static void demonstrateDataInsertion() {
        System.out.println("\n3. 📝 DEMONSTRATING DATA INSERTION...");
        
        // Add a new driver
        String newDriverQuery = 
            "PREFIX f1: <http://f1.example.org/>\n" +
            "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
            "INSERT DATA {\n" +
            "  f1:GeorgeRussell a f1:Driver ;\n" +
            "    rdfs:label \"George Russell\" ;\n" +
            "    f1:firstName \"George\" ;\n" +
            "    f1:lastName \"Russell\" ;\n" +
            "    f1:nationality \"British\" ;\n" +
            "    f1:driverNumber \"63\" ;\n" +
            "    f1:team f1:Mercedes ;\n" +
            "    f1:worldChampionships \"0\" ;\n" +
            "    f1:active \"true\" ;\n" +
            "    f1:debutYear \"2019\" .\n" +
            "}";
        
        try {
            UpdateRequest update = UpdateFactory.create(newDriverQuery);
            UpdateExecutionFactory.createRemote(update, SPARQL_UPDATE_URL).execute();
            System.out.println("✅ Added new driver: George Russell");
            
            // Show updated driver list
            System.out.println("\n--- UPDATED DRIVER LIST ---");
            String updatedDriversQuery = 
                "PREFIX f1: <http://f1.example.org/>\n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "SELECT ?name ?number ?nationality ?teamName\n" +
                "WHERE {\n" +
                "  ?driver a f1:Driver ;\n" +
                "          rdfs:label ?name ;\n" +
                "          f1:driverNumber ?number ;\n" +
                "          f1:nationality ?nationality ;\n" +
                "          f1:team ?team .\n" +
                "  ?team rdfs:label ?teamName .\n" +
                "}\n" +
                "ORDER BY ?teamName ?name";
            
            executeQuery(updatedDriversQuery);
            
        } catch (Exception e) {
            System.out.println("❌ Error adding driver: " + e.getMessage());
        }
    }
    
    private static void executeQuery(String queryString) {
        try {
            Query query = QueryFactory.create(queryString);
            try (QueryExecution qexec = QueryExecutionFactory.sparqlService(SPARQL_QUERY_URL, query)) {
                ResultSet results = qexec.execSelect();
                
                java.util.List<String> columnNames = results.getResultVars();
                
                // Print headers
                for (String columnName : columnNames) {
                    System.out.printf("%-20s", columnName);
                }
                System.out.println();
                
                // Print separator
                for (int i = 0; i < columnNames.size(); i++) {
                    System.out.printf("%-20s", "--------------------");
                }
                System.out.println();
                
                // Print results
                int rowCount = 0;
                while (results.hasNext() && rowCount < 10) { // Limit to 10 rows for demo
                    QuerySolution solution = results.next();
                    for (String columnName : columnNames) {
                        String value = solution.get(columnName) != null ? 
                                     solution.get(columnName).toString() : "N/A";
                        // Clean up URIs
                        if (value.startsWith("http://")) {
                            value = value.substring(value.lastIndexOf("/") + 1);
                        }
                        if (value.length() > 18) {
                            value = value.substring(0, 15) + "...";
                        }
                        System.out.printf("%-20s", value);
                    }
                    System.out.println();
                    rowCount++;
                }
                
                if (rowCount == 10) {
                    System.out.println("... (showing first 10 results)");
                }
            }
        } catch (Exception e) {
            System.out.println("❌ Query error: " + e.getMessage());
        }
    }
}