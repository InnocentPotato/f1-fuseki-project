package com.f1.fuseki;

import org.apache.jena.query.*;
import org.apache.jena.rdfconnection.RDFConnection;
import org.apache.jena.rdfconnection.RDFConnectionFactory;
import org.apache.jena.update.UpdateExecutionFactory;
import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateRequest;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

/**
 * F1 Fuseki Manager - Client for Apache Jena Fuseki 5.6.0
 */
public class F1FusekiManager {
    
    private static final String FUSEKI_ENDPOINT = "http://localhost:3030/f1";
    private static final String SPARQL_QUERY_URL = FUSEKI_ENDPOINT + "/sparql";
    private static final String SPARQL_UPDATE_URL = FUSEKI_ENDPOINT + "/update";
    
    public static void main(String[] args) {
        System.out.println("=========================================");
        System.out.println("  F1 Fuseki 5.6.0 Database Manager");
        System.out.println("=========================================\n");
        
        try {
            // Check if Fuseki server is running
            if (!isServerRunning()) {
                System.out.println("❌ Fuseki server is not running!");
                System.out.println("Please start Fuseki server first using:");
                System.out.println("  start-fuseki.bat");
                return;
            }
            
            System.out.println("✅ Connected to Fuseki server at " + FUSEKI_ENDPOINT);
            
            // Initialize database with sample data
            initializeDatabase();
            
            // Run demonstration queries
            runDemoQueries();
            
            System.out.println("\n🎉 F1 Fuseki 5.6.0 Demo Completed!");
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
            try (RDFConnection conn = RDFConnectionFactory.connect(FUSEKI_ENDPOINT)) {
                QueryExecution qexec = conn.query(query);
                return qexec.execAsk();
            }
        } catch (Exception e) {
            return false;
        }
    }
    
    private static void initializeDatabase() {
        System.out.println("\n1. 📥 INITIALIZING F1 DATABASE...");
        
        try (RDFConnection conn = RDFConnectionFactory.connect(FUSEKI_ENDPOINT)) {
            // Clear any existing data
            String clearQuery = "DELETE WHERE { ?s ?p ?o }";
            conn.update(UpdateFactory.create(clearQuery));
            
            // Insert comprehensive F1 data
            String f1Data = 
                "PREFIX f1: <http://f1.example.org/> " +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
                "INSERT DATA { " +
                
                "f1:LewisHamilton a f1:Driver ; " +
                "  rdfs:label \"Lewis Hamilton\" ; " +
                "  f1:firstName \"Lewis\" ; " +
                "  f1:lastName \"Hamilton\" ; " +
                "  f1:nationality \"British\" ; " +
                "  f1:driverNumber \"44\" ; " +
                "  f1:team f1:Mercedes ; " +
                "  f1:worldChampionships \"7\" ; " +
                "  f1:active \"true\" ; " +
                "  f1:debutYear \"2007\" . " +
                
                "f1:MaxVerstappen a f1:Driver ; " +
                "  rdfs:label \"Max Verstappen\" ; " +
                "  f1:firstName \"Max\" ; " +
                "  f1:lastName \"Verstappen\" ; " +
                "  f1:nationality \"Dutch\" ; " +
                "  f1:driverNumber \"1\" ; " +
                "  f1:team f1:RedBull ; " +
                "  f1:worldChampionships \"3\" ; " +
                "  f1:active \"true\" ; " +
                "  f1:debutYear \"2015\" . " +
                
                "f1:CharlesLeclerc a f1:Driver ; " +
                "  rdfs:label \"Charles Leclerc\" ; " +
                "  f1:firstName \"Charles\" ; " +
                "  f1:lastName \"Leclerc\" ; " +
                "  f1:nationality \"Monegasque\" ; " +
                "  f1:driverNumber \"16\" ; " +
                "  f1:team f1:Ferrari ; " +
                "  f1:worldChampionships \"0\" ; " +
                "  f1:active \"true\" ; " +
                "  f1:debutYear \"2018\" . " +
                
                "f1:LandoNorris a f1:Driver ; " +
                "  rdfs:label \"Lando Norris\" ; " +
                "  f1:firstName \"Lando\" ; " +
                "  f1:lastName \"Norris\" ; " +
                "  f1:nationality \"British\" ; " +
                "  f1:driverNumber \"4\" ; " +
                "  f1:team f1:McLaren ; " +
                "  f1:worldChampionships \"0\" ; " +
                "  f1:active \"true\" ; " +
                "  f1:debutYear \"2019\" . " +
                
                "f1:Mercedes a f1:Team ; " +
                "  rdfs:label \"Mercedes-AMG Petronas\" ; " +
                "  f1:base \"Brackley, United Kingdom\" ; " +
                "  f1:teamPrincipal \"Toto Wolff\" ; " +
                "  f1:championships \"8\" ; " +
                "  f1:engine \"Mercedes\" . " +
                
                "f1:RedBull a f1:Team ; " +
                "  rdfs:label \"Red Bull Racing\" ; " +
                "  f1:base \"Milton Keynes, United Kingdom\" ; " +
                "  f1:teamPrincipal \"Christian Horner\" ; " +
                "  f1:championships \"6\" ; " +
                "  f1:engine \"Honda RBPT\" . " +
                
                "f1:Ferrari a f1:Team ; " +
                "  rdfs:label \"Scuderia Ferrari\" ; " +
                "  f1:base \"Maranello, Italy\" ; " +
                "  f1:teamPrincipal \"Frédéric Vasseur\" ; " +
                "  f1:championships \"16\" ; " +
                "  f1:engine \"Ferrari\" . " +
                
                "f1:McLaren a f1:Team ; " +
                "  rdfs:label \"McLaren F1 Team\" ; " +
                "  f1:base \"Woking, United Kingdom\" ; " +
                "  f1:teamPrincipal \"Andrea Stella\" ; " +
                "  f1:championships \"8\" ; " +
                "  f1:engine \"Mercedes\" . " +
                "}";
            
            conn.update(UpdateFactory.create(f1Data));
            System.out.println("✅ F1 database initialized successfully!");
            
        } catch (Exception e) {
            System.out.println("❌ Error initializing database: " + e.getMessage());
        }
    }
    
    private static void runDemoQueries() {
        System.out.println("\n2. 🔍 RUNNING F1 DEMO QUERIES...");
        
        // Query 1: All drivers with teams
        System.out.println("\n--- ALL F1 DRIVERS ---");
        String driversQuery = 
            "PREFIX f1: <http://f1.example.org/> " +
            "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
            "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> " +
            "SELECT ?name ?number ?nationality ?teamName ?championships " +
            "WHERE { " +
            "  ?driver a f1:Driver ; " +
            "          rdfs:label ?name ; " +
            "          f1:driverNumber ?number ; " +
            "          f1:nationality ?nationality ; " +
            "          f1:team ?team ; " +
            "          f1:worldChampionships ?championships . " +
            "  ?team rdfs:label ?teamName . " +
            "} " +
            "ORDER BY DESC(xsd:integer(?championships))";
        
        executeQuery(driversQuery);
        
        // Query 2: Teams by championships
        System.out.println("\n--- F1 TEAMS RANKING ---");
        String teamsQuery = 
            "PREFIX f1: <http://f1.example.org/> " +
            "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
            "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> " +
            "SELECT ?teamName ?base ?principal ?championships ?engine " +
            "WHERE { " +
            "  ?team a f1:Team ; " +
            "        rdfs:label ?teamName ; " +
            "        f1:base ?base ; " +
            "        f1:teamPrincipal ?principal ; " +
            "        f1:championships ?championships ; " +
            "        f1:engine ?engine . " +
            "} " +
            "ORDER BY DESC(xsd:integer(?championships))";
        
        executeQuery(teamsQuery);
        
        // Query 3: Drivers by nationality
        System.out.println("\n--- DRIVERS BY NATIONALITY ---");
        String nationalityQuery = 
            "PREFIX f1: <http://f1.example.org/> " +
            "SELECT ?nationality (COUNT(?driver) as ?driverCount) " +
            "WHERE { " +
            "  ?driver a f1:Driver ; " +
            "          f1:nationality ?nationality . " +
            "} " +
            "GROUP BY ?nationality " +
            "ORDER BY DESC(?driverCount)";
        
        executeQuery(nationalityQuery);
        
        // Query 4: Add and show new driver
        demonstrateUpdates();
    }
    
    private static void demonstrateUpdates() {
        System.out.println("\n3. 📝 DEMONSTRATING LIVE UPDATES...");
        
        // Add a new driver
        String newDriver = 
            "PREFIX f1: <http://f1.example.org/> " +
            "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
            "INSERT DATA { " +
            "  f1:GeorgeRussell a f1:Driver ; " +
            "    rdfs:label \"George Russell\" ; " +
            "    f1:firstName \"George\" ; " +
            "    f1:lastName \"Russell\" ; " +
            "    f1:nationality \"British\" ; " +
            "    f1:driverNumber \"63\" ; " +
            "    f1:team f1:Mercedes ; " +
            "    f1:worldChampionships \"0\" ; " +
            "    f1:active \"true\" ; " +
            "    f1:debutYear \"2019\" . " +
            "}";
        
        try {
            UpdateRequest update = UpdateFactory.create(newDriver);
            UpdateExecutionFactory.createRemote(update, SPARQL_UPDATE_URL).execute();
            System.out.println("✅ Added new driver: George Russell (#63)");
            
            // Show updated Mercedes drivers
            System.out.println("\n--- MERCEDES DRIVERS (UPDATED) ---");
            String mercedesDrivers = 
                "PREFIX f1: <http://f1.example.org/> " +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
                "SELECT ?name ?number ?nationality " +
                "WHERE { " +
                "  ?driver a f1:Driver ; " +
                "          rdfs:label ?name ; " +
                "          f1:driverNumber ?number ; " +
                "          f1:nationality ?nationality ; " +
                "          f1:team f1:Mercedes . " +
                "} " +
                "ORDER BY ?number";
            
            executeQuery(mercedesDrivers);
            
        } catch (Exception e) {
            System.out.println("❌ Error adding driver: " + e.getMessage());
        }
    }
    
    private static void executeQuery(String queryString) {
        try {
            Query query = QueryFactory.create(queryString);
            try (RDFConnection conn = RDFConnectionFactory.connect(FUSEKI_ENDPOINT)) {
                QueryExecution qexec = conn.query(query);
                ResultSet results = qexec.execSelect();
                
                java.util.List<String> columnNames = results.getResultVars();
                
                // Print headers
                System.out.println();
                for (String columnName : columnNames) {
                    System.out.printf("%-25s", columnName);
                }
                System.out.println();
                
                // Print separator
                for (int i = 0; i < columnNames.size(); i++) {
                    System.out.printf("%-25s", "-------------------------");
                }
                System.out.println();
                
                // Print results
                while (results.hasNext()) {
                    QuerySolution solution = results.next();
                    for (String columnName : columnNames) {
                        String value = solution.get(columnName) != null ? 
                                     solution.get(columnName).toString() : "N/A";
                        // Clean up URIs for display
                        if (value.startsWith("http://")) {
                            value = value.substring(value.lastIndexOf("/") + 1);
                        }
                        System.out.printf("%-25s", value);
                    }
                    System.out.println();
                }
            }
        } catch (Exception e) {
            System.out.println("❌ Query error: " + e.getMessage());
        }
    }
}