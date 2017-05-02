import java.util.ArrayList;

public class Coloriage {
	// Sommets et Arretes du graphe
	static Sommet A = new Sommet("A");
	static Sommet B = new Sommet("B");
	static Sommet C = new Sommet("C");
	static Sommet D = new Sommet("D");
	static Arrete ab = new Arrete(A,B,false);
	static Arrete bc = new Arrete(B,C,false);
	static Arrete cd = new Arrete(C,D,true);
	static Arrete db = new Arrete(D,B,false);
	static Arrete ac = new Arrete(A,C,false);

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
	public static class Arrete {
		boolean isPref;
		Sommet s1;
		Sommet s2;

		public Arrete(Sommet s1, Sommet s2, boolean pref){this.s1 = s1; this.s2 = s2; this.isPref = pref;}

		public String toString() {
			return "" + s1.toString() + "--"+ isPref +"--" + s2.toString();
		}
	}


	/* -------------------------- */
	/* --------- GRAPHE --------- */
	/* -------------------------- */
	public static class Graphe {
		ArrayList<Sommet> sommets = new ArrayList<Sommet>();
		ArrayList<Arrete> arretes = new ArrayList<Arrete>();
		ArrayList<String> couleurs = new ArrayList<String>();

		public Graphe() {
			sommets.add(A); sommets.add(B); sommets.add(C); sommets.add(D);
			arretes.add(ab); arretes.add(bc); arretes.add(cd); arretes.add(db); arretes.add(ac);
			couleurs.add("RED"); couleurs.add("GREEN"); 
			//couleurs.add("BLUE") ; couleurs.add("YELLOW");
		}

		public Graphe(Graphe g) {
			this.sommets = g.sommets;
			this.arretes = g.arretes;
			this.couleurs = g.couleurs;
		}

		public String toString() {
			String res;
			res = "Sommets : " + 	sommets.get(0).toString() + " , " + 
									sommets.get(1).toString() + " , " + 
									sommets.get(2).toString() + " , " +
									sommets.get(3).toString(); 

			res += "\nArretes : " 	+ arretes.get(0).toString() + " , " 
									+ arretes.get(1).toString() + " , " 
									+ arretes.get(2).toString() + " , " 
									+ arretes.get(3).toString();
			return res;
		}

		// Renvoie un sommet du graphe trivialement coloriable s'il existe, sinon null
		public Sommet somTrivial(int nbCoul) {
			Sommet res = null;

			for(Sommet s : sommets) {

				// Comptage du nombre d'arretes dans lesquelles le sommet est présent
				int nbApparition = 0;

				for(Arrete ar : arretes) {
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

		public void removeArretesSom(Sommet s) {
			ArrayList<Arrete> arretesToRemove = new ArrayList<Arrete>();
			for(Arrete ar : this.arretes) {
				if(ar.s1 == s || ar.s2 == s) {
					arretesToRemove.add(ar);
				}
			}
			this.arretes.removeAll(arretesToRemove);
		}

		// Colorie les sommets du graphe
		public void colorier(Graphe g,int nbCoul) {
			Sommet s = somTrivial(nbCoul);
			if(s != null) {

				System.out.println("Sommet " + s.toString() + " trivialement colorable.");

				Graphe grapheSaved = new Graphe(this);
				this.removeArretesSom(s);
				this.sommets.remove(s);
				colorier(this, nbCoul);

				// Coloriage de s
				ArrayList<String> couleursProches = new ArrayList<String>();
				for(Arrete ar : arretes) {
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
					ArrayList<String> listeCouleursGraphe = new ArrayList<String>(this.couleurs);
					listeCouleursGraphe.removeAll(couleursProches);
					s.couleur = listeCouleursGraphe.get(0);
				}

			}

			else if(!this.sommets.isEmpty()) {
				
				// Choix du sommet de plus haut degré 
				// Comptage du nombre d'arretes dans lesquelles le sommet est présent
				int nbApparitionMax = 0;
				for(Sommet sMax : sommets) {
					int nbApparition = 0;

					for(Arrete ar : arretes) {
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

				System.out.println("Pas de TrivCol, on vire "+s.toString());

				Graphe grapheSaved = new Graphe(this);
				this.removeArretesSom(s);
				this.sommets.remove(s);
				colorier(this, nbCoul);

				// Y a-t-il une couleur dispo ? 
				// Si non, on spille
				// Coloriage de s
				ArrayList<String> couleursProches = new ArrayList<String>();
				for(Arrete ar : arretes) {
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
				// Il prend une des couleurs autres que les proches
				if(s.couleur == "void") {
					ArrayList<String> listeCouleursGraphe = new ArrayList<String>(this.couleurs);
					listeCouleursGraphe.removeAll(couleursProches);
					if(listeCouleursGraphe.isEmpty()){
						s.couleur = "spilled";
					}
					else {
						s.couleur = listeCouleursGraphe.get(0);
					}
				}




			}

			/* Coloriage optimiste
				if(exist(sommetTrivialColoriable s)) {
					colorier(G / s)
					s.couleur = une couleur dispo
				}
				else if(exist(unSommetDansGs)){
					colorier(G / s)
					if(pasDeCouleurDispo()) {
						spiller(s) avec s le plus haut degré
					}
					else {
						s.couleur = une couleur dispo
					}
				}
			*/

		}


	}




	public static void main(String[] args) {
		// Graphe déjà saisi dans le code
		Graphe graphe = new Graphe();
		//System.out.println(graphe.toString());
		graphe.colorier(graphe,2);
		System.out.println(A + " , " + B + " , " + C + " , " + D);


	}
}
