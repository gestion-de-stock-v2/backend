import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../services/api.service';
import { Category } from '../../models/category.model';

@Component({
  selector: 'app-categories',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './categories.component.html',
  styleUrls: ['./categories.component.css'],
})
export class CategoriesComponent implements OnInit {
  categories: Category[] = [];
  draft: Category = { name: '', description: '' };
  editingId: number | null = null;
  error = '';

  constructor(private api: ApiService) {}

  ngOnInit(): void { this.load(); }

  load(): void {
    this.api.getCategories().subscribe({
      next: d => (this.categories = d),
      error: () => (this.error = 'Erreur de chargement des catégories'),
    });
  }

  save(): void {
    if (!this.draft.name.trim()) return;
    this.error = '';
    const done = () => { this.cancel(); this.load(); };
    const fail = (e: any) => (this.error = e?.error?.message || 'Erreur');

    if (this.editingId) {
      this.api.updateCategory(this.editingId, this.draft).subscribe({ next: done, error: fail });
    } else {
      this.api.createCategory(this.draft).subscribe({ next: done, error: fail });
    }
  }

  edit(c: Category): void {
    this.editingId = c.id!;
    this.draft = { name: c.name, description: c.description };
  }

  remove(id: number): void {
    if (confirm('Supprimer cette catégorie ?')) {
      this.api.deleteCategory(id).subscribe({
        next: () => this.load(),
        error: e => (this.error = e?.error?.message || 'Erreur suppression'),
      });
    }
  }

  cancel(): void {
    this.editingId = null;
    this.draft = { name: '', description: '' };
  }
}
