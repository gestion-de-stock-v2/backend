import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../services/api.service';
import { Supplier } from '../../models/supplier.model';

const EMPTY: Supplier = { name: '', registrationNumber: '', phone: '', email: '' };

@Component({
  selector: 'app-fornecedores',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './fornecedores.component.html',
  styleUrls: ['./fornecedores.component.css'],
})
export class FornecedoresComponent implements OnInit {
  suppliers: Supplier[] = [];
  draft: Supplier = { ...EMPTY };
  editingId: number | null = null;
  error = '';

  constructor(private api: ApiService) {}

  ngOnInit(): void { this.load(); }

  load(): void {
    this.api.getSuppliers().subscribe({
      next: d => (this.suppliers = d),
      error: () => (this.error = 'Erreur de chargement des fournisseurs'),
    });
  }

  save(): void {
    if (!this.draft.name.trim()) return;
    this.error = '';
    const done = () => { this.cancel(); this.load(); };
    const fail = (e: any) => (this.error = e?.error?.message || 'Erreur');

    if (this.editingId) {
      this.api.updateSupplier(this.editingId, this.draft).subscribe({ next: done, error: fail });
    } else {
      this.api.createSupplier(this.draft).subscribe({ next: done, error: fail });
    }
  }

  edit(s: Supplier): void {
    this.editingId = s.id!;
    this.draft = { ...s };
  }

  remove(id: number): void {
    if (confirm('Supprimer ce fournisseur ?')) {
      this.api.deleteSupplier(id).subscribe({
        next: () => this.load(),
        error: e => (this.error = e?.error?.message || 'Erreur suppression'),
      });
    }
  }

  cancel(): void {
    this.editingId = null;
    this.draft = { ...EMPTY };
  }
}
