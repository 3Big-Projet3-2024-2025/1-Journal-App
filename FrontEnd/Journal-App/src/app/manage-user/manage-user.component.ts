import { Component, OnInit } from '@angular/core';
import { UsersService } from '../services/users.service';
import { User } from '../models/user';
import { Role } from '../models/role';

@Component({
  selector: 'app-manage-user',
  templateUrl: './manage-user.component.html',
  styleUrls: ['./manage-user.component.css'],
})
export class ManageUserComponent implements OnInit {
  users: User[] = [];
  roles: Role[] = [];
  selectedUser: User | null = null; // Stocke l'utilisateur sélectionné pour édition

  constructor(private userService: UsersService) {}

  ngOnInit(): void {
    this.loadUsers();
    this.loadRoles();
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

  loadRoles(): void {
    this.userService.getRoles().subscribe(
      (data) => {
        this.roles = data; // Charger les rôles depuis le backend
        console.log('Roles loaded:', data);
      },
      (error) => {
        console.error('Error loading roles:', error);
      }
    );
  }

  editUser(user: User): void {
    this.selectedUser = { ...user }; // Copie des données pour éviter de modifier directement
  }

  updateUser(): void {
    if (this.selectedUser) {
      // Assurez-vous que le rôle contient bien roleId et roleName
      const updatedUser = {
        ...this.selectedUser,
        role: {
          roleId: this.selectedUser.role.roleId,
          roleName: this.selectedUser.role.roleName,
        },
      };
      console.log('Role before sending:', updatedUser.role);

      this.userService.updateUser(this.selectedUser.userId, updatedUser).subscribe(
        () => {
          alert('User updated successfully!');
          this.selectedUser = null;
          this.loadUsers(); // Rafraîchir la liste après la mise à jour
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
          
          alert('User deleted successfully!');
          this.loadUsers();
        },
        (error) => {
          console.error('Error deleting user:', error);
        }
      );
    }
  }
}
