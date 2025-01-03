import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GpdrrequestComponent } from './gpdrrequest.component';

describe('GpdrrequestComponent', () => {
  let component: GpdrrequestComponent;
  let fixture: ComponentFixture<GpdrrequestComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [GpdrrequestComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(GpdrrequestComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
