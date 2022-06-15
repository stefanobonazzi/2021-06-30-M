package it.polito.tdp.genes.model;

import java.util.ArrayList;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.genes.db.GenesDao;

public class Model {

	private GenesDao dao;
	private Graph<Integer, DefaultWeightedEdge> graph;
	private List<Integer> vertices;
	private double maxWeight = 0;
	private double minWeight = 100;
	private List<Integer> result;
	private double max;
	
	public Model() {
		this.dao = new GenesDao();
	}
	
	public String creaGrafo() {
		this.graph = new SimpleDirectedWeightedGraph<Integer, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		this.vertices = this.dao.getAllChromosome();
		
		Graphs.addAllVertices(this.graph, this.vertices);
		
		for(int i: this.vertices) {
			for(int j: this.vertices) {
				if(i != j) {
					DefaultWeightedEdge edge;
				
					double weight = this.dao.getEdgeWeight(i, j);
					if(weight != 0.0) {
						if(weight < this.minWeight)
							this.minWeight = weight;
						if(weight > this.maxWeight)
							this.maxWeight = weight;
						
						edge = this.graph.addEdge(i, j);
						this.graph.setEdgeWeight(edge, weight);
					}	
					
				}
			}
		}
		
		return "Grafo creato: "+this.graph.vertexSet().size()+" vertici, "+this.graph.edgeSet().size()+" archi\n"
				+"Peso minimo = "+this.minWeight+", peso massimo = "+this.maxWeight;
	}

	public double getMaxWeight() {
		return maxWeight;
	}

	public double getMinWeight() {
		return minWeight;
	}

	public String contaArchi(double s) {
		int magg = 0;
		int min = 0;
		for(DefaultWeightedEdge e: this.graph.edgeSet()) {
			if(this.graph.getEdgeWeight(e) > s) {
				magg++;
			} else if (this.graph.getEdgeWeight(e) < s) {
				min++;
			}
		}
		return "Soglia: "+s+" --> Maggiori "+magg+", minori "+min;
	}

	public List<Integer> ricerca(double s) {
		this.result = new ArrayList<Integer>();
		this.max = 0.0;
		List<DefaultWeightedEdge> archiPossibili = new ArrayList<DefaultWeightedEdge>();
		for(DefaultWeightedEdge e: this.graph.edgeSet()) {
			if(this.graph.getEdgeWeight(e) > s) 
				archiPossibili.add(e);
		}
		this.result = new ArrayList<Integer>();
		List<Integer> parziale = new ArrayList<Integer>(); 
		for(Integer i: this.vertices) {
			parziale.add(i);
			this.ricorsiva(parziale, archiPossibili, false, 0);
			parziale.remove(i);
		}
		return this.result;
	}
	
	public void ricorsiva(List<Integer> parziale, List<DefaultWeightedEdge> archiPossibili, boolean stop, double weight) {
		if(stop) {
			if(weight > this.max) {
				this.result = new ArrayList<Integer>(parziale);
				this.max = weight;
			}
			return;
		} else {
			for(Integer i: this.vertices) {
				if(i != parziale.get(parziale.size()-1) && !parziale.contains(i)) {
					DefaultWeightedEdge edge = this.graph.getEdge(parziale.get(parziale.size()-1), i);
					if(edge != null && archiPossibili.contains(edge)) {
						parziale.add(i);
						weight += this.graph.getEdgeWeight(edge);
						this.ricorsiva(parziale, archiPossibili, stop, weight);
						weight -= this.graph.getEdgeWeight(edge);
						parziale.remove(i);
					} else {
						this.ricorsiva(parziale, archiPossibili, true, weight);
					}
				}
			}
		}		
	}
	
}