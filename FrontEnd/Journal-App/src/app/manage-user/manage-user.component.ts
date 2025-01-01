import { Component, OnInit } from '@angular/core';
import { UsersService } from '../services/users.service';
import { User } from '../models/user';

@Component({
  selector: 'app-manage-user',
  templateUrl: './manage-user.component.html',
  styleUrls: ['./manage-user.component.css'],
})
export class ManageUserComponent implements OnInit {
  users: User[] = [];
  selectedUser: User | null = null; // Stocke l'utilisateur sélectionné pour édition

  constructor(private userService: UsersService) {}

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {
    this.userService.getAllUsers().subscribe(
      (data) => {
        this.users = data;
      },
      (error) => {
        console.error('Error loading users:', error);
      }
    );
  }

  editUser(user: User): void {
    this.selectedUser = { ...user }; // Copie des données pour éviter de modifier directement
  }

  updateUser(): void {
    if (this.selectedUser) {
      this.userService.updateUser(this.selectedUser.userId, this.selectedUser).subscribe(
        () => {
          alert('User updated successfully!');
          this.selectedUser = null; // Fermer le formulaire après succès
          this.loadUsers(); // Rafraîchir la liste des utilisateurs
        },
        (error) => {
          console.error('Error updating user:', error);
        }
      );
    }
  }

  cancelEdit(): void {
    this.selectedUser = null; // Annuler l'édition
  }

  deleteUser(userId: number): void {
    if (confirm('Are you sure you want to delete this user?')) {
      this.userService.deleteUser(userId).subscribe(
        () => {
          this.loadUsers();
          alert('User deleted successfully!');
        },
        (error) => {
          console.error('Error deleting user:', error);
        }
      );
    }
  }
}
