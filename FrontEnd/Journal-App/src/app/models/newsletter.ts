// src/app/models/newsletter.ts
import { Article } from "./article";
import { User } from "./user";

export interface Newsletter {
    newsletterId: number;
    title: string;
    subtitle: string;
    publicationDate: Date; // Format: "YYYY-MM-DD"
    creator?: number;
    backgroundColor?: string; // Optionnel
    font?: string; // Optionnel
    titleFont?: string; // Optionnel
    titleFontSize?: number; // Optionnel
    titleColor?: string; // Optionnel
    titleBold?: boolean; // Optionnel
    titleUnderline?: boolean; // Optionnel
    subtitleFont?: string; // Optionnel
    subtitleFontSize?: number; // Optionnel
    subtitleColor?: string; // Optionnel
    subtitleBold?: boolean; // Optionnel
    subtitleItalic?: boolean; // Optionnel
    textAlign?: string; // Optionnel
    journalists: User[];
}
