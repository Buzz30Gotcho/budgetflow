import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CategoryService } from '../../core/category.service';
import { Category } from '../../core/models';

@Component({
  selector: 'app-categories',
  imports: [FormsModule],
  templateUrl: './categories.html',
})
export class Categories implements OnInit {
  private categoryService = inject(CategoryService);

  private palette = [
    '#6366F1', '#22C55E', '#EF4444', '#F59E0B', '#3B82F6',
    '#EC4899', '#14B8A6', '#8B5CF6', '#F97316', '#10B981',
  ];

  categories = signal<Category[]>([]);
  name = '';
  color = this.palette[0];
  error = signal<string | null>(null);

  editingId = signal<number | null>(null);
  editName = '';
  editColor = '';

  ngOnInit() {
    this.load();
  }

  load() {
    this.categoryService.list().subscribe(list => {
      this.categories.set(list);
      // Propose une couleur différente de la palette pour la prochaine catégorie.
      this.color = this.palette[list.length % this.palette.length];
    });
  }

  add() {
    if (!this.name.trim()) return;
    this.error.set(null);
    this.categoryService.create({ name: this.name, color: this.color }).subscribe({
      next: () => { this.name = ''; this.load(); },
      error: (e) => this.error.set(e?.error?.message ?? 'Erreur'),
    });
  }

  remove(id: number) {
    this.categoryService.delete(id).subscribe(() => this.load());
  }

  startEdit(cat: Category) {
    this.editingId.set(cat.id);
    this.editName = cat.name;
    this.editColor = cat.color;
  }

  cancelEdit() {
    this.editingId.set(null);
  }

  saveEdit(id: number) {
    if (!this.editName.trim()) return;
    this.categoryService.update(id, { name: this.editName, color: this.editColor }).subscribe({
      next: () => { this.editingId.set(null); this.load(); },
      error: (e) => this.error.set(e?.error?.message ?? 'Erreur'),
    });
  }
}
