import java.util.ArrayList;

public class Coloriage {
	// Sommets et Aretes du graphe
	static Sommet A = new Sommet("A");
	static Sommet B = new Sommet("B");
	static Sommet C = new Sommet("C");
	static Sommet D = new Sommet("D");
	static Arete ab = new Arete(A,B,false);
	static Arete bc = new Arete(B,C,false);
	static Arete cd = new Arete(C,D,true);
	static Arete db = new Arete(D,B,false);
	static Arete ac = new Arete(A,C,false);

	/*
	Graphe testé :

		A ---------	B
		  \		   /|					
    		\	 /	|
			  \/    |
		     / \	|
		   /  	 \	|
		 /		   \|
		D --pref--- C

		(D,C) est une arrête de préférence

	/* -------------------------- */
	/* --------- SOMMET --------- */
	/* -------------------------- */
	public static class Sommet {
		String nom;
		String couleur;

		public Sommet(String n) { this.nom = n; this.couleur = "void"; }

		public String toString() {
			return "" + nom + "|" + couleur;
		}
	}

	/* -------------------------- */
	/* --------- ARRETE --------- */
	/* -------------------------- */
	public static class Arete {
		boolean isPref;
		Sommet s1;
		Sommet s2;

		public Arete(Sommet s1, Sommet s2, boolean pref){this.s1 = s1; this.s2 = s2; this.isPref = pref;}

		public String toString() {
			return "" + s1.toString() + "--"+ isPref +"--" + s2.toString();
		}
	}


	/* -------------------------- */
	/* --------- GRAPHE --------- */
	/* -------------------------- */
	public static class Graphe {
		ArrayList<Sommet> sommets = new ArrayList<Sommet>();
		ArrayList<Arete> aretes = new ArrayList<Arete>();
		ArrayList<String> couleurs = new ArrayList<String>();

		public Graphe(int nbCoul) {
			sommets.add(A); sommets.add(B); sommets.add(C); sommets.add(D);
			aretes.add(ab); aretes.add(bc); aretes.add(cd); aretes.add(db); aretes.add(ac);
			if(nbCoul >= 1) couleurs.add("RED"); 
			if(nbCoul >= 2)	couleurs.add("GREEN"); 
			if(nbCoul >= 3)	couleurs.add("YELLOW"); 
			if(nbCoul >= 4)	couleurs.add("BLUE"); 
			if(nbCoul >= 5)	couleurs.add("PURPLE"); 
		}

		public Graphe(Graphe g) {
			this.sommets = new ArrayList<Sommet>(g.sommets);
			this.aretes = new ArrayList<Arete>(g.aretes);
			this.couleurs = new ArrayList<String>(g.couleurs);
		}

		public String toString() {
			String res = "\nSommets : ";
			for(Sommet s : this.sommets) {
				res += s.toString() + "  "; 
			}
			res += "\nAretes : ";

			for(Arete ar : this.aretes) {
				res += ar.toString() + "  \n"; 
			}
			return res;
		}

		// Renvoie un sommet du graphe trivialement coloriable s'il existe, sinon null
		public Sommet somTrivial(int nbCoul) {
			Sommet res = null;

			for(Sommet s : sommets) {

				// Comptage du nombre d'aretes dans lesquelles le sommet est présent
				int nbApparition = 0;

				for(Arete ar : aretes) {
					if(ar.s1 == s || ar.s2 == s){
						nbApparition++;
					}
				}

				if(nbApparition < nbCoul) {
					res = s;
					break;
				}
			}

			return res;
		}

		// Remove toutes les aretes qui concernent un sommet
		public void removeAretesSom(Sommet s) {
			ArrayList<Arete> aretesToRemove = new ArrayList<Arete>();
			for(Arete ar : this.aretes) {
				if(ar.s1 == s || ar.s2 == s) {
					aretesToRemove.add(ar);
				}
			}
			this.aretes.removeAll(aretesToRemove);
		}

		// Colorie les sommets du graphe
		public void colorier(Graphe g,int nbCoul) {
			Sommet s = somTrivial(nbCoul);
			if(s != null) {

				Graphe grapheSaved = new Graphe(this);
	
				this.removeAretesSom(s);
				this.sommets.remove(s);
				colorier(this, nbCoul);

				// Coloriage de s
				ArrayList<String> couleursProches = new ArrayList<String>();
				for(Arete ar : grapheSaved.aretes) {
					// s dans l'arrête ?
					if(ar.s1 == s) {
						// Arrête de préférence ?
						if(ar.isPref) {
							s.couleur = ar.s2.couleur;
						}
						else {
							couleursProches.add(ar.s2.couleur);
						}
					}
					else if (ar.s2 == s) {
						// Arrête de préférence ?
						if(ar.isPref) {
							s.couleur = ar.s1.couleur;
						}
						else {
							couleursProches.add(ar.s1.couleur);
						}
					}
				}

				// s n'apparaît dans aucune arrête de préférence et on connaît les couleurs proches
				// Il prend une des couleurs autres que les proches
				if(s.couleur == "void") {
					ArrayList<String> listeCouleursGraphe = new ArrayList<String>(grapheSaved.couleurs);
					listeCouleursGraphe.removeAll(couleursProches);
					s.couleur = listeCouleursGraphe.get(0);
				}

			}

			else if(!this.sommets.isEmpty()) {
				
				// Choix du sommet de plus haut degré 
				// Comptage du nombre d'aretes dans lesquelles le sommet est présent
				int nbApparitionMax = 0;
				for(Sommet sMax : sommets) {
					int nbApparition = 0;

					for(Arete ar : aretes) {
						if(ar.s1 == sMax || ar.s2 == sMax){
							if(!ar.isPref) {
								nbApparition++;
							}
						}
					}

					if(nbApparitionMax <= nbApparition) {
						s = sMax;
						nbApparitionMax = nbApparition;
					}
				}

				// On sauve le graphe complet et on appelle récursivement sur this sans le sommet
				Graphe grapheSaved = new Graphe(this);
				this.removeAretesSom(s);
				this.sommets.remove(s);
				colorier(this, nbCoul);						

				// Y a-t-il une couleur dispo ? 
				// Coloriage de s
				ArrayList<String> couleursProches = new ArrayList<String>();
				for(Arete ar : grapheSaved.aretes) {
					// s dans l'arrête ?
					if(ar.s1 == s) {
						// Arrête de préférence ?
						if(ar.isPref) {;
							s.couleur = ar.s2.couleur;
						}
						else {
							couleursProches.add(ar.s2.couleur);
						}
					}
					else if (ar.s2 == s) {
						// Arrête de préférence ?
						if(ar.isPref) {
							s.couleur = ar.s1.couleur;
						}
						else {
							couleursProches.add(ar.s1.couleur);
						}
					}
				}

				// s n'apparaît dans aucune arrête de préférence et on connaît les couleurs proches
				// Il prend une des couleurs autres que les proches si possible, sinon il est spillé
				if(s.couleur == "void") {
					ArrayList<String> listeCouleursGraphe = new ArrayList<String>(grapheSaved.couleurs);
					listeCouleursGraphe.removeAll(couleursProches);
					if(listeCouleursGraphe.isEmpty()){
						s.couleur = "spilled";
					}
					else {
						s.couleur = listeCouleursGraphe.get(0);
					}
				}
			}
		}
	}

	public static void main(String[] args) {
		// Graphe déjà saisi dans le code
		int nbCoul = 2;

		Graphe graphe = new Graphe(nbCoul);
		System.out.println(graphe.toString());
		graphe.colorier(graphe,nbCoul);
		System.out.println(A + " , " + B + " , " + C + " , " + D);
	}
}
