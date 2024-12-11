import { TestBed } from '@angular/core/testing';

import { ManageNewsletterService } from './manage-newsletter.service';

describe('ManageNewsletterService', () => {
  let service: ManageNewsletterService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ManageNewsletterService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
