import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ManageRgpdFormComponent } from './manage-rgpd-form.component';

describe('ManageRgpdFormComponent', () => {
  let component: ManageRgpdFormComponent;
  let fixture: ComponentFixture<ManageRgpdFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ManageRgpdFormComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ManageRgpdFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
