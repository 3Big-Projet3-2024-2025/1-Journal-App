import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ManageJournalistComponent } from './manage-journalist.component';

describe('ManageJournalistComponent', () => {
  let component: ManageJournalistComponent;
  let fixture: ComponentFixture<ManageJournalistComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ManageJournalistComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ManageJournalistComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
