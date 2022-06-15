package it.polito.tdp.genes.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import it.polito.tdp.genes.model.Genes;

public class GenesDao {
	
	public List<Genes> getAllGenes(){
		String sql = "SELECT DISTINCT GeneID, Essential, Chromosome FROM Genes";
		List<Genes> result = new ArrayList<Genes>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Genes genes = new Genes(res.getString("GeneID"), 
						res.getString("Essential"), 
						res.getInt("Chromosome"));
				result.add(genes);
			}
			res.close();
			st.close();
			conn.close();
			return result;
			
		} catch (SQLException e) {
			throw new RuntimeException("Database error", e) ;
		}
	}
	
	public List<Integer> getAllChromosome(){
		String sql = "SELECT DISTINCT `Chromosome` "
				+ "FROM `genes` "
				+ "WHERE `Chromosome` != 0";
		List<Integer> result = new ArrayList<Integer>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {
				result.add(res.getInt("Chromosome"));
			}
			res.close();
			st.close();
			conn.close();
			return result;
			
		} catch (SQLException e) {
			throw new RuntimeException("Database error", e) ;
		}
	}

	public double getEdgeWeight(int c1, int c2){
		String sql = "SELECT i.`Expression_Corr` as weight "
				+ "FROM `interactions` i "
				+ "WHERE i.`GeneID1` IN (SELECT `GeneID` "
				+ "					  	 FROM `genes` "
				+ "					     WHERE `Chromosome` = ?) AND i.`GeneID2` IN (SELECT `GeneID` "
				+ "					 											     FROM `genes` "
				+ "																     WHERE `Chromosome` = ?) "
				+ "GROUP BY i.`GeneID1`";
		double weight = 0.0;
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, c1);
			st.setInt(2, c2);
			ResultSet res = st.executeQuery();
			while (res.next()) {
				weight += res.getDouble("weight");
			}
			res.close();
			st.close();
			conn.close();
			return weight;
			
		} catch (SQLException e) {
			throw new RuntimeException("Database error", e) ;
		}
	}
}
