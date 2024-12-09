import { Role } from "./role";
import { Newsletter } from "./newsletter";
import { Article } from "./article";

export interface User {

    userId: number;
    firstName: string;
    lastName: string;
    dateOfBirth: string;
    email: string;
    password: string;
    newPassword?: string;
    longitude: number;
    latitude: number;
    isAuthorized: boolean;
    isRoleChange: boolean;
    keycloakId: string;
    role: Role;
    articles?: Article[];  
    newsletters?: Newsletter[]; 
}
