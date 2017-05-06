import java.util.ArrayList;

public class Coloriage {

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
			String res = "" + s1.toString() + "---";
			if(isPref)res+="p";
			res += "---" + s2.toString();
			return res;
		}
	}

	/* -------------------------- */
	/* --------- GRAPHE --------- */
	/* -------------------------- */
	public static class Graphe {
		String name;

		ArrayList<Sommet> sommets = new ArrayList<Sommet>();
		ArrayList<Arete> aretes = new ArrayList<Arete>();
		ArrayList<String> couleurs = new ArrayList<String>();

		// Ces deux listes ne sont jamais modifiées par les fonctions (contrairement à
		// sommets et aretes qui sont vidées par colorier())
		ArrayList<Arete> aretesFixes = new ArrayList<Arete>();
		ArrayList<Sommet> sommetsFixes = new ArrayList<Sommet>();

		// Constructeur de base d'un GRaphe
		public Graphe(String n, int nbCoul, ArrayList<Sommet> listSom, ArrayList<Arete> listAr) {
			this.name = n;

			for(Sommet s : listSom) {
				sommets.add(s);
				sommetsFixes.add(s);
			}
			for(Arete a : listAr) {
				aretes.add(a);
				aretesFixes.add(a);
			}

			// Le nombre de couleurs disponibles varie
			if(nbCoul >= 1) couleurs.add("RED");
			if(nbCoul >= 2)	couleurs.add("GREEN");
			if(nbCoul >= 3)	couleurs.add("YELLOW");
			if(nbCoul >= 4)	couleurs.add("BLUE");
			if(nbCoul >= 5)	couleurs.add("PURPLE");
			if(nbCoul >= 6)	couleurs.add("WHITE");
		}

		// Constructeur de Graphe par recopie
		public Graphe(Graphe g) {
			this.name = g.name;
			this.sommets = new ArrayList<Sommet>(g.sommets);
			this.sommetsFixes = new ArrayList<Sommet>(g.sommetsFixes);
			this.aretes = new ArrayList<Arete>(g.aretes);
			this.aretesFixes = new ArrayList<Arete>(g.aretesFixes);
			this.couleurs = new ArrayList<String>(g.couleurs);
		}

		public String toString() {
			String res = "\n---- GRAPHE " + this.name + " ---\nSommets : ";
			for(Sommet s : this.sommetsFixes) {
				res += s.toString() + "  ";
			}
			res += "\nAretes : \n";

			for(Arete ar : this.aretesFixes) {
				res += ar.toString() + "  \n";
			}
			return res;
		}

		// Renvoie un sommet du graphe trivialement coloriable s'il existe, sinon null
		public Sommet somTrivial(int nbCoul) {
			Sommet res = null;
			for(Sommet s : sommets) {

				// Détermiation du degré d'un sommet (ommission des arrêtes de préférence)
				int nbApparition = 0;

				for(Arete ar : aretes) {
					if(ar.s1 == s || ar.s2 == s){
						if(!ar.isPref) {
							nbApparition++;
						}
					}
				}

				if(nbApparition < nbCoul) {
					res = s;
					break;
				}
			}

			return res;
		}

		// Supprime toutes les aretes qui concernent un sommet
		public void removeAretesSom(Sommet s) {
			ArrayList<Arete> aretesToRemove = new ArrayList<Arete>();
			for(Arete ar : this.aretes) {
				if(ar.s1.equals(s) || ar.s2.equals(s)) {
					aretesToRemove.add(ar);
				}
			}
			this.aretes.removeAll(aretesToRemove);
		}

		// Colorie les sommets du graphe (fonction récursive)
		public void colorier(Graphe g,int nbCoul) {
			Sommet s = somTrivial(nbCoul);
			if(s != null) {

				this.removeAretesSom(s);
				this.sommets.remove(s);
				colorier(this, nbCoul);

				// Récupération du sommet dans sommetsFixes
				for(Sommet sF : sommetsFixes) {
					if(sF.equals(s)){
						s = sF;
					}
				}

				// Coloriage de s
				ArrayList<String> couleursProches = new ArrayList<String>();
				for(Arete ar : this.aretesFixes) {
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

				// On appelle récursivement sur this sans le sommet
				this.removeAretesSom(s);
				this.sommets.remove(s);
				colorier(this, nbCoul);

				// Récupération du sommet dans sommetsFixes
				for(Sommet sF : sommetsFixes) {
					if(sF.equals(s)){
						s = sF;
					}
				}

				// Y a-t-il une couleur dispo ?
				// Coloriage de s
				ArrayList<String> couleursProches = new ArrayList<String>();
				for(Arete ar : this.aretesFixes) {
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
		}
	}

	public static void main(String[] args) {

		/* ===== GRAPHE1 ===== */
		/*
			A ---------	B
			  \		   /|
	    		\	 /	|
				  \/    |
			     / \	|
			   /  	 \	|
			 /		   \|
			D --pref--- C

			(D,C) est une arête de préférence
		*/

		/* Nb de couleurs avec lequel vous souhaitez colorier le graphe 1*/
		int nbCoulGraphe1 = 3;

		ArrayList<Sommet> listSom1 = new ArrayList<Sommet>();
		Sommet A = new Sommet("A");
		Sommet B = new Sommet("B");
		Sommet C = new Sommet("C");
		Sommet D = new Sommet("D");
		listSom1.add(A);
		listSom1.add(B);
		listSom1.add(C);
		listSom1.add(D);

		ArrayList<Arete> listAr1 = new ArrayList<Arete>();
		listAr1.add(new Arete(B,C,false));
		listAr1.add(new Arete(C,D,true));
		listAr1.add(new Arete(D,B,false));
		listAr1.add(new Arete(A,C,false));
		listAr1.add(new Arete(A,B,false));

		Graphe graphe1 = new Graphe("1",nbCoulGraphe1,listSom1,listAr1);

		/* ===== GRAPHE2 ===== */
		/*
						X ---------	Y
					  /	  \		   /|
			Z	     /		\	 /	|
			|		/		  \/    |
			|	   /	     / \	|
			V ----  	   /  	 \	|
			  \			 /		   \|
				--------T --pref--- U

			(T,U) est une arête de préférence
		*/
		int nbCoulGraphe2 = 2;

		ArrayList<Sommet> listSom2 = new ArrayList<Sommet>();
		Sommet X = new Sommet("X");
		Sommet Y = new Sommet("Y");
		Sommet Z = new Sommet("Z");
		Sommet T = new Sommet("T");
		Sommet U = new Sommet("U");
		Sommet V = new Sommet("V");
		listSom2.add(X);
		listSom2.add(Y);
		listSom2.add(Z);
		listSom2.add(T);
		listSom2.add(U);
		listSom2.add(V);

		ArrayList<Arete> listAr2 = new ArrayList<Arete>();
		listAr2.add(new Arete(Z,V,false));
		listAr2.add(new Arete(V,X,false));
		listAr2.add(new Arete(V,T,false));
		listAr2.add(new Arete(X,Y,false));
		listAr2.add(new Arete(X,U,false));
		listAr2.add(new Arete(Y,T,false));
		listAr2.add(new Arete(Y,U,false));
		listAr2.add(new Arete(T,U,true));

		Graphe graphe2 = new Graphe("2",nbCoulGraphe2,listSom2,listAr2);


		/* ===== GRAPHE 3 ===== */
		/*
			l ---------	m
			|  	   		|
	    	|		 	|
			|	    	|
			|    		|
			|    	 	|
			| 		   	|
			o --------- n
		*/
		int nbCoulGraphe3 = 2;

		ArrayList<Sommet> listSom3 = new ArrayList<Sommet>();
		Sommet l = new Sommet("l");
		Sommet m = new Sommet("m");
		Sommet n = new Sommet("n");
		Sommet o = new Sommet("o");
		listSom3.add(l);
		listSom3.add(m);
		listSom3.add(n);
		listSom3.add(o);

		ArrayList<Arete> listAr3 = new ArrayList<Arete>();
		listAr3.add(new Arete(l,m,false));
		listAr3.add(new Arete(m,n,false));
		listAr3.add(new Arete(n,o,false));
		listAr3.add(new Arete(o,l,false));

		Graphe graphe3 = new Graphe("3",nbCoulGraphe3,listSom3,listAr3);



		System.out.println(graphe1.toString());
		graphe1.colorier(graphe1,nbCoulGraphe1);
		System.out.println("Après coloriage avec "+ nbCoulGraphe1 + " couleurs : \n" + graphe1.toString()+"\n\n");

		System.out.println(graphe2.toString());
		graphe2.colorier(graphe2,nbCoulGraphe2);
		System.out.println("Après coloriage avec "+ nbCoulGraphe2 + " couleurs : \n" + graphe2.toString()+"\n\n");

		System.out.println(graphe3.toString());
		graphe3.colorier(graphe3,nbCoulGraphe3);
		System.out.println("Après coloriage avec "+ nbCoulGraphe3 + " couleurs : \n" + graphe3.toString()+"\n\n");
	}
}
