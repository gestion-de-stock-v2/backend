export interface Supplier {
  id?: number;
  name: string;
  /** Numero d'identification de l'entreprise (ex-champ cnpj). */
  registrationNumber?: string;
  phone?: string;
  email?: string;
}
